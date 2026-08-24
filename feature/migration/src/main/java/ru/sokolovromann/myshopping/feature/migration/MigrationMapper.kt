package ru.sokolovromann.myshopping.feature.migration

import android.os.Environment
import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.API
import ru.sokolovromann.myshopping.core.domain.model.AddEditProductPreferences
import ru.sokolovromann.myshopping.core.domain.model.AfterAddingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterAddingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterArchivingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterCompletingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterCompletingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterEditingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByCartCheckbox
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductCheckbox
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductEnter
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductItem
import ru.sokolovromann.myshopping.core.domain.model.BackupDirectory
import ru.sokolovromann.myshopping.core.domain.model.BackupPreferences
import ru.sokolovromann.myshopping.core.domain.model.CalculateProductsTotal
import ru.sokolovromann.myshopping.core.domain.model.Cart
import ru.sokolovromann.myshopping.core.domain.model.CartBudget
import ru.sokolovromann.myshopping.core.domain.model.CartDirectory
import ru.sokolovromann.myshopping.core.domain.model.CartDiscount
import ru.sokolovromann.myshopping.core.domain.model.CartPriority
import ru.sokolovromann.myshopping.core.domain.model.CartReminder
import ru.sokolovromann.myshopping.core.domain.model.CartTotal
import ru.sokolovromann.myshopping.core.domain.model.CartsPreferences
import ru.sokolovromann.myshopping.core.domain.model.CartsProductsDisplayMode
import ru.sokolovromann.myshopping.core.domain.model.CartsView
import ru.sokolovromann.myshopping.core.domain.model.CheckboxColor
import ru.sokolovromann.myshopping.core.domain.model.Currency
import ru.sokolovromann.myshopping.core.domain.model.DateTimeFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.DeletionCartFromTrash
import ru.sokolovromann.myshopping.core.domain.model.DiscountMeasurementUnit
import ru.sokolovromann.myshopping.core.domain.model.DisplaySuggestionDetails
import ru.sokolovromann.myshopping.core.domain.model.DisplaySuggestionNames
import ru.sokolovromann.myshopping.core.domain.model.Fabric
import ru.sokolovromann.myshopping.core.domain.model.FabricDirectory
import ru.sokolovromann.myshopping.core.domain.model.FabricValue
import ru.sokolovromann.myshopping.core.domain.model.FilterProductsByStatus
import ru.sokolovromann.myshopping.core.domain.model.FontSize
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.model.GroupCartsByStatus
import ru.sokolovromann.myshopping.core.domain.model.GroupProductsByStatus
import ru.sokolovromann.myshopping.core.domain.model.KeyboardDisplayDelay
import ru.sokolovromann.myshopping.core.domain.model.LockProductField
import ru.sokolovromann.myshopping.core.domain.model.MoneyFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.Position
import ru.sokolovromann.myshopping.core.domain.model.Product
import ru.sokolovromann.myshopping.core.domain.model.ProductDirectory
import ru.sokolovromann.myshopping.core.domain.model.ProductDiscount
import ru.sokolovromann.myshopping.core.domain.model.ProductPriority
import ru.sokolovromann.myshopping.core.domain.model.ProductQuantity
import ru.sokolovromann.myshopping.core.domain.model.ProductStatus
import ru.sokolovromann.myshopping.core.domain.model.ProductsAddingMode
import ru.sokolovromann.myshopping.core.domain.model.ProductsPreferences
import ru.sokolovromann.myshopping.core.domain.model.ProductsTotalCalculatingMode
import ru.sokolovromann.myshopping.core.domain.model.ProductsView
import ru.sokolovromann.myshopping.core.domain.model.ProductsWidgetPreferences
import ru.sokolovromann.myshopping.core.domain.model.RepeatCartReminder
import ru.sokolovromann.myshopping.core.domain.model.SortCarts
import ru.sokolovromann.myshopping.core.domain.model.SortProducts
import ru.sokolovromann.myshopping.core.domain.model.SortSuggestions
import ru.sokolovromann.myshopping.core.domain.model.StrikethroughCompletedProducts
import ru.sokolovromann.myshopping.core.domain.model.Suggestion
import ru.sokolovromann.myshopping.core.domain.model.SuggestionAddingMode
import ru.sokolovromann.myshopping.core.domain.model.SuggestionDirectory
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsFieldsDisplayMode
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsPreferences
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsView
import ru.sokolovromann.myshopping.core.domain.model.SwipeCart
import ru.sokolovromann.myshopping.core.domain.model.SwipeCartActionName
import ru.sokolovromann.myshopping.core.domain.model.SwipeProduct
import ru.sokolovromann.myshopping.core.domain.model.SwipeProductActionName
import ru.sokolovromann.myshopping.core.domain.model.Tax
import ru.sokolovromann.myshopping.core.domain.model.Theme
import ru.sokolovromann.myshopping.core.domain.model.TimeInMillis
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.model.UserConfig
import ru.sokolovromann.myshopping.core.domain.model.UserPreferences
import ru.sokolovromann.myshopping.old.api15.model.AppBuildConfig
import ru.sokolovromann.myshopping.old.api15.model.Autocomplete
import ru.sokolovromann.myshopping.old.api15.model.Shopping
import ru.sokolovromann.myshopping.old.api15.model.SuggestionDetail
import java.math.RoundingMode
import java.text.DecimalFormat

class MigrationMapper @Inject constructor() {

    fun toCarts(shoppings: Collection<Shopping>): Collection<Cart> = shoppings.map {
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
        val sort: SortProducts? = if (it.sortBy.isNotEmpty()) {
            when (it.sortBy) {
                "ByCreated" -> SortProducts.ByCreated(it.sortAscending)
                "ByLastModified" -> SortProducts.ByLastModified(it.sortAscending)
                "ByName" -> SortProducts.ByName(it.sortAscending)
                "ByCost" -> SortProducts.ByCost(it.sortAscending)
                "DoNotSort" -> SortProducts.DoNotSort
                else -> SortProducts.DoNotSort
            }
        } else null
        Cart(
            UID(it.uid),
            directory,
            Position(it.position),
            TimeInMillis(it.lastModified),
            TimeInMillis(it.lastModified),
            priority,
            it.name,
            reminder,
            discount,
            total,
            budget,
            "",
            sort,
            null
        )
    }

    fun toProducts(api15Products: Collection<Api15Product>): Collection<Product> = api15Products.map {
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
        Product(
            UID(it.productUid),
            ProductDirectory(UID(it.shoppingUid)),
            Position(it.position),
            TimeInMillis(it.lastModified),
            TimeInMillis(it.lastModified),
            status,
            priority,
            it.name,
            quantity,
            unitPrice,
            fullPrice,
            discount,
            tax,
            cost,
            it.note,
            it.manufacturer,
            it.brand,
            it.size,
            it.color
        )
    }

    fun toSuggestionsFromOld(api15Suggestions: Collection<Api15Suggestion>): Collection<Suggestion> =
        api15Suggestions.map {
            Suggestion(
                UID(it.uid),
                SuggestionDirectory.NoDirectory,
                TimeInMillis(it.created.toLongOrNull() ?: 0L),
                TimeInMillis(it.lastModified.toLongOrNull() ?: 0L),
                it.name,
                it.used.toIntOrNull() ?: 0
            )
        }

    fun toSuggestionsWithFabricsFromAutocompletes(autocompletes: Collection<Autocomplete>): Map<Suggestion, Collection<Fabric>> =
        autocompletes
            .groupBy { autocomplete -> autocomplete.name.lowercase() }
            .mapKeys { autocompletes ->
                val name = autocompletes.key.replaceFirstChar { it.uppercase() }
                val used = autocompletes.value.count()
                val currentDateTime = TimeInMillis.getCurrent()
                Suggestion(
                    UID.createRandom(),
                    SuggestionDirectory.NoDirectory,
                    currentDateTime,
                    currentDateTime,
                    name,
                    used
                )
            }
            .mapValues { autocompletes ->
                createFabrics(autocompletes)
            }

    fun toFabricsFromDetails(details: Collection<SuggestionDetail>): Collection<Fabric> = details.map {
        val value: FabricValue = when (it.type) {
            "Quantity" -> {
                val quantity = it.value.toBigDecimal()
                val quantitySymbol = it.valueParams
                val data = ProductQuantity(quantity, quantitySymbol)
                FabricValue.QuantityType(data)
            }
            "UnitPrice" -> {
                val data = it.value.toBigDecimal()
                FabricValue.UnitPriceType(data)
            }
            "Discount" -> {
                val discount = it.value.toBigDecimal()
                val type: DiscountMeasurementUnit = if (it.valueParams.toBooleanStrict()) {
                    DiscountMeasurementUnit.Percent
                } else {
                    DiscountMeasurementUnit.Money
                }
                val data = ProductDiscount(discount, type)
                FabricValue.DiscountType(data)
            }
            "TaxRate" -> {
                val tax = it.value.toBigDecimal()
                val data = Tax(tax)
                FabricValue.TaxType(data)
            }
            "Cost" -> {
                val data = it.value.toBigDecimal()
                FabricValue.CostType(data)
            }
            "Manufacturer" -> {
                FabricValue.ManufacturerType(it.value)
            }
            "Brand" -> {
                FabricValue.BrandType(it.value)
            }
            "Size" -> {
                FabricValue.SizeType(it.value)
            }
            "Color" -> {
                FabricValue.ColorType(it.value)
            }
            else -> FabricValue.NoData
        }
        Fabric(
            UID(it.uid),
            FabricDirectory(UID(it.directory)),
            TimeInMillis(it.created.toLong()),
            TimeInMillis(it.lastModified.toLong()),
            value,
            it.used.toInt()
        )
    }

    fun toUserConfig(appBuildConfig: AppBuildConfig) = UserConfig(
        appBuildConfig.userCodeVersion?.toLong()?.let { API(it) }
    )

    fun toUserPreferences(api15UserPreferences: Api15UserPreferences) = UserPreferences(
        createGeneralPreferences(api15UserPreferences),
        createCartsPreferences(api15UserPreferences),
        createProductsPreferences(api15UserPreferences),
        createProductsWidgetPreferences(api15UserPreferences),
        createAddEditProductPreferences(api15UserPreferences),
        createSuggestionsPreferences(api15UserPreferences),
        createBackupPreferences()
    )

    private fun createFabrics(entry: Map.Entry<Suggestion, List<Autocomplete>>) = mutableListOf<Fabric>().apply {
        val directory = FabricDirectory(entry.key.uid)
        val autocompletes = entry.value
        addAll(createQuantities(directory, autocompletes))
        addAll(createUnitPrices(directory, autocompletes))
        addAll(createDiscounts(directory, autocompletes))
        addAll(createTaxRates(directory, autocompletes))
        addAll(createCosts(directory, autocompletes))
        addAll(createManufacturers(directory, autocompletes))
        addAll(createBrands(directory, autocompletes))
        addAll(createSizes(directory, autocompletes))
        addAll(createColors(directory, autocompletes))
    }

    private fun createQuantities(
        directory: FabricDirectory,
        autocompletes: List<Autocomplete>
    ): List<Fabric> {
        val filtered = autocompletes.filter { it.quantity > 0f }
        return filtered
            .distinctBy { getQuantityDecimalFormat().format(it.quantity.toBigDecimal()) }
            .map { autocomplete ->
                val quantity = autocomplete.quantity.toBigDecimal()
                val quantitySymbol = autocomplete.quantitySymbol
                val used = filtered.count { it.quantity == autocomplete.quantity }
                val productQuantity = ProductQuantity(quantity, quantitySymbol)
                createFabric(directory, FabricValue.QuantityType(productQuantity), used)
            }
    }

    private fun createUnitPrices(
        directory: FabricDirectory,
        autocompletes: List<Autocomplete>
    ): List<Fabric> {
        val filtered = autocompletes.filter { it.price > 0f }
        return filtered
            .distinctBy { getMoneyDecimalFormat().format(it.price.toBigDecimal()) }
            .map { autocomplete ->
                val data = autocomplete.price.toBigDecimal()
                val used = filtered.count { it.price == autocomplete.price }
                createFabric(directory, FabricValue.UnitPriceType(data), used)
            }
    }

    private fun createDiscounts(
        directory: FabricDirectory,
        autocompletes: List<Autocomplete>
    ): List<Fabric> {
        val filtered = autocompletes.filter { it.discount > 0f }
        return filtered
            .distinctBy { getMoneyDecimalFormat().format(it.discount.toBigDecimal()) }
            .map { autocomplete ->
                val discount = autocomplete.discount.toBigDecimal()
                val type: DiscountMeasurementUnit = if (autocomplete.discountAsPercent) {
                    DiscountMeasurementUnit.Percent
                } else {
                    DiscountMeasurementUnit.Money
                }
                val productDiscount = ProductDiscount(discount, type)
                val used = filtered.count { it.discount == autocomplete.discount }
                createFabric(directory, FabricValue.DiscountType(productDiscount), used)
            }
    }

    private fun createTaxRates(
        directory: FabricDirectory,
        autocompletes: List<Autocomplete>
    ): List<Fabric> {
        val filtered = autocompletes.filter { it.taxRate > 0f }
        return filtered
            .distinctBy { getMoneyDecimalFormat().format(it.taxRate.toBigDecimal()) }
            .map { autocomplete ->
                val data = autocomplete.taxRate.toBigDecimal()
                val tax = Tax(data)
                val used = filtered.count { it.taxRate == autocomplete.taxRate }
                createFabric(directory, FabricValue.TaxType(tax), used)
            }
    }

    private fun createCosts(
        directory: FabricDirectory,
        autocompletes: List<Autocomplete>
    ): List<Fabric> {
        val filtered = autocompletes.filter { it.total > 0f }
        return filtered
            .distinctBy { getMoneyDecimalFormat().format(it.total.toBigDecimal()) }
            .map { autocomplete ->
                val data = autocomplete.total.toBigDecimal()
                val used = filtered.count { it.total == autocomplete.total }
                createFabric(directory, FabricValue.CostType(data), used)
            }
    }

    private fun createManufacturers(
        directory: FabricDirectory,
        autocompletes: List<Autocomplete>
    ): List<Fabric> {
        val filtered = autocompletes.filter { it.manufacturer.isNotEmpty() }
        return filtered
            .distinctBy { it.manufacturer.lowercase() }
            .map { autocomplete ->
                val data = autocomplete.manufacturer
                val used = filtered.count { it.manufacturer.equals(autocomplete.manufacturer, true) }
                createFabric(directory, FabricValue.ManufacturerType(data), used)
            }
    }

    private fun createBrands(
        directory: FabricDirectory,
        autocompletes: List<Autocomplete>
    ): List<Fabric> {
        val filtered = autocompletes.filter { it.brand.isNotEmpty() }
        return filtered
            .distinctBy { it.brand.lowercase() }
            .map { autocomplete ->
                val data = autocomplete.brand
                val used = filtered.count { it.brand.equals(autocomplete.brand, true) }
                createFabric(directory, FabricValue.BrandType(data), used)
            }
    }

    private fun createSizes(
        directory: FabricDirectory,
        autocompletes: List<Autocomplete>
    ): List<Fabric> {
        val filtered = autocompletes.filter { it.size.isNotEmpty() }
        return filtered
            .distinctBy { it.size.lowercase() }
            .map { autocomplete ->
                val data = autocomplete.size
                val used = filtered.count { it.size.equals(autocomplete.size, true) }
                createFabric(directory, FabricValue.SizeType(data), used)
            }
    }

    private fun createColors(
        directory: FabricDirectory,
        autocompletes: List<Autocomplete>
    ): List<Fabric> {
        val filtered = autocompletes.filter { it.color.isNotEmpty() }
        return filtered
            .distinctBy { it.color.lowercase() }
            .map { autocomplete ->
                val data = autocomplete.color
                val used = filtered.count { it.color.equals(autocomplete.color, true) }
                createFabric(directory, FabricValue.ColorType(data), used)
            }
    }

    private fun createFabric(directory: FabricDirectory, value: FabricValue, used: Int): Fabric {
        val timeInMillis = TimeInMillis.getCurrent()
        return Fabric(
            UID.createRandom(),
            directory,
            timeInMillis,
            timeInMillis,
            value,
            used
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

    private fun createGeneralPreferences(api15UserPreferences: Api15UserPreferences): GeneralPreferences {
        val theme = if (api15UserPreferences.nightTheme == true) Theme.Dark else Theme.Light
        val fontSize = when (api15UserPreferences.fontSize) {
            "SMALL" -> FontSize.Small
            "MEDIUM" -> FontSize.Medium
            "LARGE" -> FontSize.Large
            "HUGE" -> FontSize.ExtraLarge
            "HUGE_2" -> FontSize.Huge
            "HUGE_3" -> FontSize.ExtraHuge
            else -> FontSize.Medium
        }
        val moneyFormattingMode = if (api15UserPreferences.minMoneyFractionDigits == 0) {
            MoneyFormattingMode.Advanced
        } else {
            MoneyFormattingMode.Simple
        }
        val currency = if (api15UserPreferences.displayCurrencyToLeft == true) {
            Currency.Left(api15UserPreferences.currency.orEmpty())
        } else {
            Currency.Right(api15UserPreferences.currency.orEmpty())
        }
        return GeneralPreferences(
            theme,
            fontSize,
            DateTimeFormattingMode.DDMMMYYYY(is24hour = true),
            moneyFormattingMode,
            currency,
            KeyboardDisplayDelay.Ms50
        )
    }

    private fun createCartsPreferences(api15UserPreferences: Api15UserPreferences): CartsPreferences {
        val displayMode = when (api15UserPreferences.displayShoppingsProducts) {
            "COLUMNS" -> CartsProductsDisplayMode.ProductsVertically
            "ROW" -> CartsProductsDisplayMode.ProductsHorizontally
            "HIDE", "HIDE_IF_HAS_TITLE" -> CartsProductsDisplayMode.HideProducts
            else -> CartsProductsDisplayMode.ProductsHorizontally
        }
        val view = if (api15UserPreferences.shoppingsMultiColumns == true) {
            CartsView.Grid(displayMode)
        } else {
            CartsView.List(displayMode)
        }
        val sortByAscending = api15UserPreferences.shoppingsSortAscending ?: true
        val sort = when (api15UserPreferences.shoppingsSortBy) {
            "POSITION" -> SortCarts.DoNotSort
            "CREATED" -> SortCarts.ByCreated(sortByAscending)
            "LAST_MODIFIED" -> SortCarts.ByLastModified(sortByAscending)
            "NAME" -> SortCarts.ByName(sortByAscending)
            "TOTAL" -> SortCarts.ByTotal(sortByAscending)
            else -> SortCarts.DoNotSort
        }
        val displayEmpty = api15UserPreferences.displayEmptyShoppings ?: true
        val groupByStatus = when (api15UserPreferences.displayCompleted) {
            "FIRST" -> GroupCartsByStatus.CompletedFirst(displayEmpty)
            "LAST" -> GroupCartsByStatus.ActiveFirst(displayEmpty)
            "HIDE" -> GroupCartsByStatus.HideCompleted(displayEmpty)
            "NO_SPLIT" -> GroupCartsByStatus.DoNotGroup(displayEmpty)
            else -> GroupCartsByStatus.ActiveFirst(displayEmpty)
        }
        val totalCalculatingMode = if (api15UserPreferences.displayLongTotal == true) {
            ProductsTotalCalculatingMode.Long
        } else {
            ProductsTotalCalculatingMode.Short
        }
        val calculateTotal = if (api15UserPreferences.displayMoney == true) {
            when (api15UserPreferences.displayTotal) {
                "ALL" -> CalculateProductsTotal.AllProducts(totalCalculatingMode)
                "COMPLETED" -> CalculateProductsTotal.CompletedProducts(totalCalculatingMode)
                "ACTIVE" -> CalculateProductsTotal.ActiveProducts(totalCalculatingMode)
                else -> CalculateProductsTotal.DoNotCalculate
            }
        } else { CalculateProductsTotal.DoNotCalculate }
        val afterAdding = when (api15UserPreferences.afterAddShopping) {
            "OPEN_PRODUCTS_SCREEN" -> AfterAddingCart.OpenProductsScreen
            "OPEN_EDIT_SHOPPING_NAME_SCREEN" -> AfterAddingCart.OpenAddNameScreen
            "OPEN_ADD_PRODUCT_SCREEN" -> AfterAddingCart.OpenAddProductScreen
            else -> AfterAddingCart.OpenProductsScreen
        }
        val afterCompleting = when (api15UserPreferences.afterShoppingCompleted) {
            "NOTHING" -> AfterCompletingCart.DoNothing
            "ARCHIVE" -> AfterCompletingCart.ArchiveCart
            "DELETE" -> AfterCompletingCart.DeleteCart
            "DELETE_PRODUCTS" -> AfterCompletingCart.DeleteProducts
            "DELETE_LIST_AND_PRODUCTS" -> AfterCompletingCart.DeleteCartAndProducts
            else -> AfterCompletingCart.DoNothing
        }
        val afterArchiving = if (api15UserPreferences.archiveAsCompleted == true) {
            AfterArchivingCart.ChangeCartStatusToCompleted
        } else {
            AfterArchivingCart.DoNothing
        }
        val checkboxColor = if (api15UserPreferences.coloredCheckbox == true) {
            CheckboxColor.RedOrGreen
        } else {
            CheckboxColor.Gray
        }
        fun swipeActionName(name: String?) = when (name) {
            "DISABLED" -> SwipeCartActionName.Off
            "ARCHIVE" -> SwipeCartActionName.ArchiveOrUnarchiveCart
            "DELETE" -> SwipeCartActionName.DeleteOrRestoreCart
            "COMPLETE" -> SwipeCartActionName.ChangeCartStatusToCompletedOrActive
            "DELETE_PRODUCTS" -> SwipeCartActionName.DeleteProducts
            else -> SwipeCartActionName.Off
        }
        val deletionFromTrash = if (api15UserPreferences.automaticallyEmptyTrash == true) {
            DeletionCartFromTrash.After7Days
        } else {
            DeletionCartFromTrash.DoNotDelete
        }
        return CartsPreferences(
            view,
            sort,
            groupByStatus,
            calculateTotal,
            afterAdding,
            afterCompleting,
            afterArchiving,
            AfterTappingByCartCheckbox.DoNothing,
            checkboxColor,
            SwipeCart.Left(swipeActionName(api15UserPreferences.swipeShoppingLeft)),
            SwipeCart.Right(swipeActionName(api15UserPreferences.swipeShoppingRight)),
            deletionFromTrash
        )
    }

    private fun createProductsPreferences(api15UserPreferences: Api15UserPreferences): ProductsPreferences {
        val view = if (api15UserPreferences.productsMultiColumns == true) {
            ProductsView.Grid
        } else {
            ProductsView.List
        }
        val groupByStatus = when (api15UserPreferences.displayCompleted) {
            "FIRST" -> GroupProductsByStatus.CompletedFirst
            "LAST" -> GroupProductsByStatus.ActiveFirst
            "HIDE" -> GroupProductsByStatus.HideCompleted
            "NO_SPLIT" -> GroupProductsByStatus.DoNotGroup
            else -> GroupProductsByStatus.ActiveFirst
        }
        val addingMode = if (api15UserPreferences.displayListOfAutocompletes == true) {
            ProductsAddingMode.Advanced
        } else {
            ProductsAddingMode.Simple
        }
        val totalCalculatingMode = if (api15UserPreferences.displayLongTotal == true) {
            ProductsTotalCalculatingMode.Long
        } else {
            ProductsTotalCalculatingMode.Short
        }
        val calculateTotal = if (api15UserPreferences.displayMoney == true) {
            when (api15UserPreferences.displayTotal) {
                "ALL" -> CalculateProductsTotal.AllProducts(totalCalculatingMode)
                "COMPLETED" -> CalculateProductsTotal.CompletedProducts(totalCalculatingMode)
                "ACTIVE" -> CalculateProductsTotal.ActiveProducts(totalCalculatingMode)
                else -> CalculateProductsTotal.DoNotCalculate
            }
        } else { CalculateProductsTotal.DoNotCalculate }
        val strikethroughCompleted = if (api15UserPreferences.strikethroughCompletedProducts == true) {
            StrikethroughCompletedProducts.On
        } else {
            StrikethroughCompletedProducts.Off
        }
        val afterCompleting = when (api15UserPreferences.afterProductCompleted) {
            "NOTHING" -> AfterCompletingProduct.DoNothing
            "EDIT" -> AfterCompletingProduct.EditProduct
            "DELETE" -> AfterCompletingProduct.DeleteProduct
            else -> AfterCompletingProduct.DoNothing
        }
        val afterTappingByCheckbox = if (api15UserPreferences.completedWithCheckbox == true) {
            AfterTappingByProductCheckbox.ChangeProductStatus
        } else {
            AfterTappingByProductCheckbox.DoNothing
        }
        val checkboxColor = if (api15UserPreferences.coloredCheckbox == true) {
            CheckboxColor.RedOrGreen
        } else {
            CheckboxColor.Gray
        }
        val afterTappingByItem = if (api15UserPreferences.completedWithCheckbox == true) {
            AfterTappingByProductItem.DoNothing
        } else {
            AfterTappingByProductItem.ChangeProductStatus
        }
        fun swipeActionName(name: String?) = when (name) {
            "DISABLED" -> SwipeProductActionName.Off
            "EDIT" -> SwipeProductActionName.EditProduct
            "DELETE" -> SwipeProductActionName.DeleteProduct
            "COMPLETE" -> SwipeProductActionName.ChangeProductStatusToCompletedOrActive
            else -> SwipeProductActionName.Off
        }
        return ProductsPreferences(
            view,
            SortProducts.DoNotSort,
            groupByStatus,
            addingMode,
            calculateTotal,
            strikethroughCompleted,
            afterCompleting,
            afterTappingByCheckbox,
            checkboxColor,
            afterTappingByItem,
            SwipeProduct.Left(swipeActionName(api15UserPreferences.swipeProductLeft)),
            SwipeProduct.Right(swipeActionName(api15UserPreferences.swipeProductRight))
        )
    }

    private fun createProductsWidgetPreferences(api15UserPreferences: Api15UserPreferences): ProductsWidgetPreferences {
        val theme = if (api15UserPreferences.widgetNightTheme == true) Theme.Dark else Theme.Light
        val fontSize = when (api15UserPreferences.widgetFontSize) {
            "SMALL" -> FontSize.Small
            "MEDIUM" -> FontSize.Medium
            "LARGE" -> FontSize.Large
            "HUGE" -> FontSize.ExtraLarge
            "HUGE_2" -> FontSize.Huge
            "HUGE_3" -> FontSize.ExtraHuge
            else -> FontSize.Medium
        }
        val groupByStatus = when (api15UserPreferences.displayCompleted) {
            "FIRST" -> GroupProductsByStatus.CompletedFirst
            "LAST" -> GroupProductsByStatus.ActiveFirst
            "HIDE" -> GroupProductsByStatus.HideCompleted
            "NO_SPLIT" -> GroupProductsByStatus.DoNotGroup
            else -> GroupProductsByStatus.ActiveFirst
        }
        return ProductsWidgetPreferences(theme, fontSize, SortProducts.DoNotSort, groupByStatus)
    }

    private fun createAddEditProductPreferences(api15UserPreferences: Api15UserPreferences): AddEditProductPreferences {
        val lockField = when (api15UserPreferences.lockProductElement) {
            "QUANTITY" -> LockProductField.Quantity
            "PRICE" -> LockProductField.UnitPrice
            "TOTAL" -> LockProductField.Cost
            else -> LockProductField.Cost
        }
        val afterTappingByEnter = if (api15UserPreferences.enterToSaveProduct == true) {
            AfterTappingByProductEnter.SaveProduct
        } else {
            AfterTappingByProductEnter.GoToNextField
        }
        val afterAdding = when (api15UserPreferences.afterSaveProduct) {
            "CLOSE_SCREEN" -> AfterAddingProduct.CloseScreen
            "OPEN_NEW_SCREEN" -> AfterAddingProduct.OpenNewScreen
            "NOTHING" -> AfterAddingProduct.DoNothing
            else -> AfterAddingProduct.DoNothing
        }
        val taxRate = api15UserPreferences.taxRate ?: 0f
        val tax = if (taxRate > 0f) Tax(taxRate.toBigDecimal()) else null
        return AddEditProductPreferences(
            lockField,
            afterTappingByEnter,
            afterAdding,
            AfterEditingProduct.CloseScreen,
            tax
        )
    }

    private fun createSuggestionsPreferences(api15UserPreferences: Api15UserPreferences): SuggestionsPreferences {
        val addingMode = if (api15UserPreferences.saveProductToAutocompletes == true) {
            SuggestionAddingMode.All
        } else {
            SuggestionAddingMode.Off
        }
        val displayNames = when (api15UserPreferences.maxAutocompletesNames) {
            0 -> DisplaySuggestionNames.DoNotDisplay
            in 1..5 -> DisplaySuggestionNames.Low
            in 6..10 -> DisplaySuggestionNames.Medium
            else -> DisplaySuggestionNames.Medium
        }
        return SuggestionsPreferences(
            SuggestionsView.List(SuggestionsFieldsDisplayMode.All),
            SortSuggestions.ByName(byAscending = true),
            addingMode,
            displayNames,
            DisplaySuggestionDetails.Medium
        )
    }

    private fun createBackupPreferences(): BackupPreferences {
        val directory = "${Environment.getExternalStorageDirectory()}/MyShoppingList"
        return BackupPreferences(BackupDirectory(directory))
    }
}