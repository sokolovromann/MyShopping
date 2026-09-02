package ru.sokolovromann.myshopping.core.data.old

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.data.datasource.CartsDao
import ru.sokolovromann.myshopping.core.data.datasource.FabricsDao
import ru.sokolovromann.myshopping.core.data.datasource.ProductsDao
import ru.sokolovromann.myshopping.core.data.datasource.SuggestionsDao
import ru.sokolovromann.myshopping.core.data.di.Api15DataStore
import ru.sokolovromann.myshopping.core.data.model.CartEntity
import ru.sokolovromann.myshopping.core.data.model.FabricEntity
import ru.sokolovromann.myshopping.core.data.model.ProductEntity
import ru.sokolovromann.myshopping.core.data.model.SuggestionEntity
import ru.sokolovromann.myshopping.core.data.old.datasource.Api15Dao
import ru.sokolovromann.myshopping.core.data.old.datasource.Api15LocalDataStoreScheme
import ru.sokolovromann.myshopping.core.data.old.datasource.Api15LocalRoomDatabase
import ru.sokolovromann.myshopping.core.data.old.model.Api15AutocompleteEntity
import ru.sokolovromann.myshopping.core.data.old.model.Api15ProductEntity
import ru.sokolovromann.myshopping.core.data.old.model.Api15ShoppingEntity
import ru.sokolovromann.myshopping.core.data.old.model.Api39SuggestionDetailEntity
import ru.sokolovromann.myshopping.core.data.old.model.Api39SuggestionEntity
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.CartBudget
import ru.sokolovromann.myshopping.core.domain.model.CartDirectory
import ru.sokolovromann.myshopping.core.domain.model.CartDiscount
import ru.sokolovromann.myshopping.core.domain.model.CartPriority
import ru.sokolovromann.myshopping.core.domain.model.CartReminder
import ru.sokolovromann.myshopping.core.domain.model.CartTotal
import ru.sokolovromann.myshopping.core.domain.model.DiscountMeasurementUnit
import ru.sokolovromann.myshopping.core.domain.model.FilterProductsByStatus
import ru.sokolovromann.myshopping.core.domain.model.ProductDiscount
import ru.sokolovromann.myshopping.core.domain.model.ProductPriority
import ru.sokolovromann.myshopping.core.domain.model.ProductQuantity
import ru.sokolovromann.myshopping.core.domain.model.ProductStatus
import ru.sokolovromann.myshopping.core.domain.model.RepeatCartReminder
import ru.sokolovromann.myshopping.core.domain.model.SuggestionDirectory
import ru.sokolovromann.myshopping.core.domain.model.Tax
import ru.sokolovromann.myshopping.core.domain.model.TimeInMillis
import ru.sokolovromann.myshopping.core.domain.model.UID
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat

class Api15RoomMigrationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api15Dao: Api15Dao,
    private val cartsDao: CartsDao,
    private val productsDao: ProductsDao,
    private val suggestionsDao: SuggestionsDao,
    private val fabricsDao: FabricsDao,
    @Api15DataStore private val api15DataStore: DataStore<Preferences>,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun migrate(): Unit = withContext(ioDispatcher) {
        val api15Shoppings = api15Dao.getShoppings()
        val carts = fromApi15ShoppingsToCarts(api15Shoppings)
        cartsDao.insertCarts(carts)

        val api15Products = api15Dao.getProducts()
        val products = fromApi15ProductsToProducts(api15Products)
        productsDao.insertProducts(products)

        val api = api15DataStore.data.first()[Api15LocalDataStoreScheme.Build.userCodeVersion]
        when (api) {
            in 15..38 -> {
                val api15Autocompletes = api15Dao.getAutocompletes()

                val suggestions = fromApi15AutocompletesToSuggestions(api15Autocompletes)
                suggestionsDao.insertSuggestions(suggestions)

                val fabrics = fromApi15AutocompletesToFabrics(api15Autocompletes)
                fabricsDao.insertFabrics(fabrics)
            }
            in 39..41 -> {
                val api39Suggestions = api15Dao.getSuggestions()
                val suggestions = fromApi39SuggestionsToSuggestions(api39Suggestions)
                suggestionsDao.insertSuggestions(suggestions)

                val api39SuggestionDetails = api15Dao.getSuggestionDetails()
                val fabrics = toApi39SuggestionDetailsToFabrics(api39SuggestionDetails)
                fabricsDao.insertFabrics(fabrics)
            }
            else -> {}
        }
    }

    fun deleteDatabase() {
        if (exists()) {
            Api15LocalRoomDatabase.build(context).close()
            getDatabaseFile().delete()
            context.getDatabasePath("${Api15LocalRoomDatabase.DATABASE_NAME}-shm").delete()
            context.getDatabasePath("${Api15LocalRoomDatabase.DATABASE_NAME}-wal").delete()
        }
    }

    fun exists(): Boolean = getDatabaseFile().exists()

    private fun getDatabaseFile(): File =
        context.getDatabasePath(Api15LocalRoomDatabase.DATABASE_NAME)

    private fun fromApi15ShoppingsToCarts(shoppings: Collection<Api15ShoppingEntity>) = shoppings.map {
        val directory = if (it.deleted) {
            CartDirectory.Deleted
        } else {
            if (it.archived) CartDirectory.Archived else CartDirectory.Current
        }
        val priority = if (it.pinned) CartPriority.High else CartPriority.Medium
        val reminder: CartReminder? = if (it.reminder > 0L) {
            val timeInMillis = TimeInMillis(it.reminder)
            CartReminder(timeInMillis, RepeatCartReminder.NoRepeat)
        } else null
        val discount: CartDiscount? = if (it.discount > 0f) {
            val measurementUnit = if (it.discountAsPercent) DiscountMeasurementUnit.Percent else DiscountMeasurementUnit.Money
            val filterByStatus: FilterProductsByStatus = try {
                enumValueOf(it.discountProducts)
            } catch (_: Exception) { FilterProductsByStatus.All }
            CartDiscount(it.discount.toBigDecimal(), measurementUnit, filterByStatus)
        } else null
        val total: CartTotal? = if (it.total > 0f) {
            CartTotal(it.total.toBigDecimal(), FilterProductsByStatus.All)
        } else null
        val budget: CartBudget? = if (it.budget > 0f) {
            val filterByStatus: FilterProductsByStatus = try {
                enumValueOf(it.budgetProducts)
            } catch (_: Exception) { FilterProductsByStatus.All }
            CartBudget(it.budget.toBigDecimal(), filterByStatus)
        } else null
        val sortProducts: String = when (it.sortBy) {
            "CREATED" -> "ByCreated"
            "LAST_MODIFIED" -> "ByLastModified"
            "NAME" -> "ByName"
            "TOTAL" -> "ByCost"
            "POSITION" -> "DoNotSort"
            else -> ""
        }
        val sortProductsByAscending = if (sortProducts.isEmpty()) "" else it.sortAscending.toString()
        CartEntity(
            uid = it.uid,
            directory = directory.toString(),
            position = it.position.toString(),
            created = it.lastModified.toString(),
            lastModified = it.lastModified.toString(),
            priority = priority.toString(),
            name = it.name,
            reminder = reminder?.time?.value?.toString().orEmpty(),
            repeatReminder = reminder?.repeat?.toString().orEmpty(),
            discount = discount?.money?.toPlainString().orEmpty(),
            discountMeasurementUnit = discount?.measurementUnit?.toString().orEmpty(),
            filterDiscountByProductStatus = discount?.filterByStatus?.toString().orEmpty(),
            total = total?.money?.toPlainString().orEmpty(),
            filterTotalByProductStatus = total?.filterByStatus?.toString().orEmpty(),
            budget = budget?.money?.toPlainString().orEmpty(),
            filterBudgetByProductStatus = budget?.filterByStatus?.toString().orEmpty(),
            note = "",
            image = "",
            sortProducts = sortProducts,
            sortProductsByAscending = sortProductsByAscending,
            groupProductsByStatus = ""
        )
    }

    private fun fromApi15ProductsToProducts(products: Collection<Api15ProductEntity>) = products.map {
        val status = if (it.completed) ProductStatus.Completed else ProductStatus.Active
        val priority = if (it.pinned) ProductPriority.High else ProductPriority.Medium
        val quantity = if (it.quantity > 0f) {
            ProductQuantity(it.quantity.toBigDecimal(), it.quantitySymbol)
        } else null
        val unitPrice = if (it.price > 0f) it.price.toBigDecimal() else null
        val totalMinusDiscountAndTax = it.quantity * it.price
        val fullPrice = if (totalMinusDiscountAndTax > 0f) {
            totalMinusDiscountAndTax.toBigDecimal()
        } else null
        val discount = if (it.discount > 0f) {
            val measurementUnit = if (it.discountAsPercent) DiscountMeasurementUnit.Percent else DiscountMeasurementUnit.Money
            ProductDiscount(it.discount.toBigDecimal(), measurementUnit)
        } else null
        val tax = if (it.taxRate > 0f) Tax(it.taxRate.toBigDecimal()) else null
        val cost = if (it.total > 0f) it.total.toBigDecimal() else null
        ProductEntity(
            uid = it.productUid,
            directory = it.shoppingUid,
            position = it.position.toString(),
            created = it.lastModified.toString(),
            lastModified = it.lastModified.toString(),
            status = status.toString(),
            priority = priority.toString(),
            name = it.name,
            quantity = quantity?.number?.toPlainString().orEmpty(),
            quantityMeasurementUnit = quantity?.measurementUnit.orEmpty(),
            unitPrice = unitPrice?.toPlainString().orEmpty(),
            fullPrice = fullPrice?.toPlainString().orEmpty(),
            discount = discount?.money?.toPlainString().orEmpty(),
            discountMeasurementUnit = discount?.measurementUnit?.toString().orEmpty(),
            tax = tax?.value?.toPlainString().orEmpty(),
            cost = cost?.toPlainString().orEmpty(),
            note = it.note,
            manufacturer = it.manufacturer,
            brand = it.brand,
            size = it.size,
            color = it.color,
            image = ""
        )
    }

    private fun fromApi15AutocompletesToSuggestions(autocompletes: Collection<Api15AutocompleteEntity>) =
        fromApi15AutocompletesToSuggestionsWithFabrics(autocompletes).flatMap { it.keys }

    private fun fromApi15AutocompletesToFabrics(autocompletes: Collection<Api15AutocompleteEntity>) =
        fromApi15AutocompletesToSuggestionsWithFabrics(autocompletes).flatMap { it.values.flatten() }

    private fun fromApi39SuggestionsToSuggestions(suggestions: Collection<Api39SuggestionEntity>) = suggestions.map {
        SuggestionEntity(
            uid = it.uid,
            directory = SuggestionDirectory.NoDirectory.toString(),
            created = it.created,
            lastModified = it.lastModified,
            name = it.name,
            used = it.used
        )
    }

    private fun toApi39SuggestionDetailsToFabrics(details: Collection<Api39SuggestionDetailEntity>) = details.map {
        val valueParams = when (it.type) {
            "Discount" -> {
                if (it.valueParams.toBooleanStrict()) {
                    DiscountMeasurementUnit.Percent.toString()
                } else {
                    DiscountMeasurementUnit.Money.toString()
                }
            }
            "TaxRate" -> ""
            else -> it.valueParams
        }
        val type = when (it.type) {
            "Quantity" -> "QuantityType"
            "UnitPrice" -> "UnitPriceType"
            "Discount" -> "DiscountType"
            "TaxRate" -> "TaxType"
            "Cost" -> "CostType"
            "Manufacturer" -> "ManufacturerType"
            "Brand" -> "BrandType"
            "Size" -> "SizeType"
            "Color" -> "ColorType"
            else -> "NoData"
        }
        FabricEntity(
            uid = it.uid,
            directory = it.directory,
            created = it.created,
            lastModified = it.lastModified,
            type = type,
            value = it.value,
            valueParams = valueParams,
            used = it.used
        )
    }

    private fun fromApi15AutocompletesToSuggestionsWithFabrics(autocompletes: Collection<Api15AutocompleteEntity>) = autocompletes.map {
        autocompletes
            .groupBy { autocomplete -> autocomplete.name.lowercase() }
            .mapKeys { autocompletes ->
                val currentDateTime = TimeInMillis.getCurrent().value.toString()
                SuggestionEntity(
                    uid = UID.createRandom().value,
                    directory = SuggestionDirectory.NoDirectory.toString(),
                    created = currentDateTime,
                    lastModified = currentDateTime,
                    name = autocompletes.key.replaceFirstChar { it.uppercase() },
                    used = autocompletes.value.count().toString()
                )
            }
            .mapValues { autocompletes ->
                val quantities = autocompletes.value.filter { it.quantity > 0f }
                val quantityTypes = quantities
                    .distinctBy { getQuantityDecimalFormat().format(it.quantity.toBigDecimal()) }
                    .map { autocomplete ->
                        createFabric().copy(
                            directory = autocompletes.key.uid,
                            type = "QuantityType",
                            value = autocomplete.quantity.toString(),
                            valueParams = autocomplete.quantitySymbol,
                            used = quantities.count { it.quantity == autocomplete.quantity }.toString()
                        )
                    }
                val unitPrices = autocompletes.value.filter { it.price > 0f }
                val unitPriceTypes = unitPrices
                    .distinctBy { getMoneyDecimalFormat().format(it.price.toBigDecimal()) }
                    .map { autocomplete ->
                        createFabric().copy(
                            directory = autocompletes.key.uid,
                            type = "UnitPriceType",
                            value = autocomplete.price.toString(),
                            used = unitPrices.count { it.price == autocomplete.price }.toString()
                        )
                    }
                val discounts = autocompletes.value.filter { it.discount > 0f }
                val discountTypes = discounts
                    .distinctBy { getMoneyDecimalFormat().format(it.discount.toBigDecimal()) }
                    .map { autocomplete ->
                        val measurementUnit = if (autocomplete.discountAsPercent) {
                            DiscountMeasurementUnit.Percent
                        } else {
                            DiscountMeasurementUnit.Money
                        }
                        createFabric().copy(
                            directory = autocompletes.key.uid,
                            type = "DiscountType",
                            value = autocomplete.discount.toString(),
                            valueParams = measurementUnit.toString()
                        )
                    }
                val taxRates = autocompletes.value.filter { it.taxRate > 0f }
                val taxTypes = taxRates
                    .distinctBy { getMoneyDecimalFormat().format(it.taxRate.toBigDecimal()) }
                    .map { autocomplete ->
                        createFabric().copy(
                            directory = autocompletes.key.uid,
                            type = "TaxType",
                            value = autocomplete.taxRate.toString()
                        )
                    }
                val costs = autocompletes.value.filter { it.total > 0f }
                val costsType = costs
                    .distinctBy { getMoneyDecimalFormat().format(it.total.toBigDecimal()) }
                    .map { autocomplete ->
                        createFabric().copy(
                            directory = autocompletes.key.uid,
                            type = "CostType",
                            value = autocomplete.total.toString()
                        )
                    }
                val manufacturers = autocompletes.value.filter { it.manufacturer.isNotEmpty() }
                val manufacturerTypes = manufacturers
                    .distinctBy { it.manufacturer.lowercase() }
                    .map { autocomplete ->
                        createFabric().copy(
                            directory = autocompletes.key.uid,
                            type = "ManufacturerType",
                            value = autocomplete.manufacturer
                        )
                    }
                val brands = autocompletes.value.filter { it.brand.isNotEmpty() }
                val brandTypes = brands
                    .distinctBy { it.brand.lowercase() }
                    .map { autocomplete ->
                        createFabric().copy(
                            directory = autocompletes.key.uid,
                            type = "BrandType",
                            value = autocomplete.brand
                        )
                    }
                val sizes = autocompletes.value.filter { it.size.isNotEmpty() }
                val sizeTypes = sizes
                    .distinctBy { it.size.lowercase() }
                    .map { autocomplete ->
                        createFabric().copy(
                            directory = autocompletes.key.uid,
                            type = "SizeType",
                            value = autocomplete.size
                        )
                    }
                val colors = autocompletes.value.filter { it.color.isNotEmpty() }
                val colorTypes = colors
                    .distinctBy { it.color.lowercase() }
                    .map { autocomplete ->
                        createFabric().copy(
                            directory = autocompletes.key.uid,
                            type = "ColorType",
                            value = autocomplete.color
                        )
                    }
                quantityTypes.toMutableList().apply {
                    addAll(unitPriceTypes)
                    addAll(discountTypes)
                    addAll(taxTypes)
                    addAll(costsType)
                    addAll(manufacturerTypes)
                    addAll(brandTypes)
                    addAll(sizeTypes)
                    addAll(colorTypes)
                }
            }
    }


    private fun createFabric(): FabricEntity {
        val currentDateTime = TimeInMillis.getCurrent().value.toString()
        return FabricEntity(
            uid = UID.createRandom().value,
            directory = "",
            created = currentDateTime,
            lastModified = currentDateTime,
            type = "",
            value = "",
            valueParams = "",
            used = ""
        )
    }

    private fun getQuantityDecimalFormat(): DecimalFormat {
        return DecimalFormat().apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 3
            roundingMode = RoundingMode.HALF_UP
        }
    }

    private fun getMoneyDecimalFormat(): DecimalFormat {
        return DecimalFormat().apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
            roundingMode = RoundingMode.HALF_UP
        }
    }
}