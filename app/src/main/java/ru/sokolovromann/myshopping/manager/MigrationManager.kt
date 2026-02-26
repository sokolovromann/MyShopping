package ru.sokolovromann.myshopping.manager

import ru.sokolovromann.myshopping.data39.old.Api15AutocompleteEntity
import ru.sokolovromann.myshopping.data39.suggestions.AddSuggestionWithDetails
import ru.sokolovromann.myshopping.data39.suggestions.Suggestion
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetail
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetailValue
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDirectory
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsConfig
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsDefaults
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsPreInstalled
import ru.sokolovromann.myshopping.data39.suggestions.TakeSuggestionDetails
import ru.sokolovromann.myshopping.data39.suggestions.TakeSuggestionDetailsInfo
import ru.sokolovromann.myshopping.data39.suggestions.TakeSuggestions
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withIoContext
import ru.sokolovromann.myshopping.utils.UID
import ru.sokolovromann.myshopping.utils.calendar.DateTime
import ru.sokolovromann.myshopping.utils.calendar.DateTimeAlias
import ru.sokolovromann.myshopping.utils.math.Decimal
import ru.sokolovromann.myshopping.utils.math.DecimalExtensions.toDecimal
import ru.sokolovromann.myshopping.utils.math.DecimalWithParams
import ru.sokolovromann.myshopping.utils.math.DiscountType
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.collections.count
import kotlin.collections.filter

class MigrationManager @Inject constructor(
    private val api15Manager: Api15Manager,
    private val suggestionsManager: SuggestionsManager
) {

    suspend fun migrateAutocompletesFromApi15(): Unit = withIoContext {
        val suggestionsWithDetails = api15Manager.getAutocompletes()
            .groupBy { it.name.lowercase() }
            .mapKeys { createSuggestion(it) }
            .mapValues { createDetails(it) }

        val suggestions = suggestionsWithDetails.keys
        suggestionsManager.addSuggestions(suggestions)

        val details = suggestionsWithDetails.flatMap { it.value }
        suggestionsManager.addDetails(details)
    }

    suspend fun migrateAutocompletesConfigFromApi15(): Unit = withIoContext {
        val api15Config = api15Manager.getAutocompletesConfig()

        val addSuggestionWithDetails = if (api15Config.saveProductToAutocompletes == null) {
            SuggestionsDefaults.ADD
        } else {
            if (api15Config.saveProductToAutocompletes) {
                AddSuggestionWithDetails.SuggestionAndDetails
            } else {
                AddSuggestionWithDetails.DoNotAdd
            }
        }

        val takeSuggestion = if (api15Config.maxAutocompletesNames == null) {
            SuggestionsDefaults.TAKE_SUGGESTIONS
        } else {
            if (api15Config.maxAutocompletesNames == 0) {
                TakeSuggestions.DoNotTake
            } else if (api15Config.maxAutocompletesNames <= 5) {
                TakeSuggestions.Five
            } else {
                TakeSuggestions.Ten
            }
        }

        fun toTakeDetails(max: Int): TakeSuggestionDetails = when (max) {
            0 -> TakeSuggestionDetails.DoNotTake
            1 -> TakeSuggestionDetails.One
            2, 3 -> TakeSuggestionDetails.Three
            4, 5 -> TakeSuggestionDetails.Five
            else -> TakeSuggestionDetails.Ten
        }
        val takeDetails = TakeSuggestionDetailsInfo(
            descriptions = toTakeDetails(api15Config.maxAutocompletesOthers ?: 0),
            quantities = toTakeDetails(api15Config.maxAutocompletesQuantities ?: 0),
            money = toTakeDetails(api15Config.maxAutocompletesMoneys ?: 0)
        )

        val config = SuggestionsConfig(
            preInstalled = SuggestionsPreInstalled.DoNotAdd,
            viewMode = SuggestionsDefaults.VIEW_MODE,
            sort = SuggestionsDefaults.SORT,
            add = addSuggestionWithDetails,
            takeSuggestions = takeSuggestion,
            takeDetails = takeDetails
        )
        suggestionsManager.addConfig(config)
    }

    private fun createSuggestion(entry: Map.Entry<String, List<Api15AutocompleteEntity>>): Suggestion {
        val name = entry.key.replaceFirstChar { it.uppercase() }
        val used = entry.value.count()
        val currentDateTime = DateTime.getCurrent()
        return Suggestion(
            uid = UID.createFromString(name),
            directory = SuggestionDirectory.NoDirectory,
            created = currentDateTime,
            lastModified = currentDateTime,
            name = name,
            used = used
        )
    }

    private fun createDetails(
        entry: Map.Entry<Suggestion, List<Api15AutocompleteEntity>>
    ): Collection<SuggestionDetail> {
        return mutableListOf<SuggestionDetail>().apply {
            val directory = entry.key.uid
            val autocompletes = entry.value

            addAll(createManufacturers(directory, autocompletes))
            addAll(createBrands(directory, autocompletes))
            addAll(createSizes(directory, autocompletes))
            addAll(createColors(directory, autocompletes))
            addAll(createQuantities(directory, autocompletes))
            addAll(createUnitPrices(directory, autocompletes))
            addAll(createDiscounts(directory, autocompletes))
            addAll(createTaxRates(directory, autocompletes))
            addAll(createCosts(directory, autocompletes))
        }
    }

    private fun createManufacturers(
        directory: UID,
        autocompletes: List<Api15AutocompleteEntity>
    ): List<SuggestionDetail.Manufacturer> {
        val filtered = autocompletes.filter { it.manufacturer.isNotEmpty() }
        return filtered
            .distinctBy { it.manufacturer.lowercase() }
            .map { autocomplete ->
                val data = autocomplete.manufacturer
                val used = filtered.count { it.manufacturer.equals(autocomplete.manufacturer, true) }
                val value = createStringSuggestionDetailValue(directory, data, used)
                SuggestionDetail.Manufacturer(value)
            }
    }

    private fun createBrands(
        directory: UID,
        autocompletes: List<Api15AutocompleteEntity>
    ): List<SuggestionDetail.Brand> {
        val filtered = autocompletes.filter { it.brand.isNotEmpty() }
        return filtered
            .distinctBy { it.brand.lowercase() }
            .map { autocomplete ->
                val data = autocomplete.brand
                val used = filtered.count { it.brand.equals(autocomplete.brand, true) }
                val value = createStringSuggestionDetailValue(directory, data, used)
                SuggestionDetail.Brand(value)
            }
    }

    private fun createSizes(
        directory: UID,
        autocompletes: List<Api15AutocompleteEntity>
    ): List<SuggestionDetail.Size> {
        val filtered = autocompletes.filter { it.size.isNotEmpty() }
        return filtered
            .distinctBy { it.size.lowercase() }
            .map { autocomplete ->
                val data = autocomplete.size
                val used = filtered.count { it.size.equals(autocomplete.size, true) }
                val value = createStringSuggestionDetailValue(directory, data, used)
                SuggestionDetail.Size(value)
            }
    }

    private fun createColors(
        directory: UID,
        autocompletes: List<Api15AutocompleteEntity>
    ): List<SuggestionDetail.Color> {
        val filtered = autocompletes.filter { it.color.isNotEmpty() }
        return filtered
            .distinctBy { it.color.lowercase() }
            .map { autocomplete ->
                val data = autocomplete.color
                val used = filtered.count { it.color.equals(autocomplete.color, true) }
                val value = createStringSuggestionDetailValue(directory, data, used)
                SuggestionDetail.Color(value)
            }
    }

    private fun createQuantities(
        directory: UID,
        autocompletes: List<Api15AutocompleteEntity>
    ): List<SuggestionDetail.Quantity> {
        val filtered = autocompletes.filter { it.quantity > 0f }
        return filtered
            .distinctBy { getQuantityDecimalFormat().format(it.quantity.toBigDecimal()) }
            .map { autocomplete ->
                val quantity = autocomplete.quantity
                val quantitySymbol = autocomplete.quantitySymbol
                val used = filtered.count { it.quantity == autocomplete.quantity }
                val value = createDecimalWithParamsSuggestionDetailValue(directory, quantity, quantitySymbol, used)
                SuggestionDetail.Quantity(value)
            }
    }

    private fun createUnitPrices(
        directory: UID,
        autocompletes: List<Api15AutocompleteEntity>
    ): List<SuggestionDetail.UnitPrice> {
        val filtered = autocompletes.filter { it.price > 0f }
        return filtered
            .distinctBy { getMoneyDecimalFormat().format(it.price.toBigDecimal()) }
            .map { autocomplete ->
                val data = autocomplete.price
                val used = filtered.count { it.price == autocomplete.price }
                val value = createDecimalSuggestionDetailValue(directory, data, used)
                SuggestionDetail.UnitPrice(value)
            }
    }

    private fun createDiscounts(
        directory: UID,
        autocompletes: List<Api15AutocompleteEntity>
    ): List<SuggestionDetail.Discount> {
        val filtered = autocompletes.filter { it.discount > 0f }
        return filtered
            .distinctBy { getMoneyDecimalFormat().format(it.discount.toBigDecimal()) }
            .map { autocomplete ->
                val discount = autocomplete.discount
                val type: DiscountType = if (autocomplete.discountAsPercent) {
                    DiscountType.Percent
                } else {
                    DiscountType.Money
                }
                val used = filtered.count { it.discount == autocomplete.discount }
                val value = createDecimalWithParamsSuggestionDetailValue(directory, discount, type, used)
                SuggestionDetail.Discount(value)
            }
    }

    private fun createTaxRates(
        directory: UID,
        autocompletes: List<Api15AutocompleteEntity>
    ): List<SuggestionDetail.TaxRate> {
        val filtered = autocompletes.filter { it.taxRate > 0f }
        return filtered
            .distinctBy {getMoneyDecimalFormat().format(it.taxRate.toBigDecimal()) }
            .map { autocomplete ->
                val data = autocomplete.taxRate
                val used = filtered.count { it.taxRate == autocomplete.taxRate }
                val value = createDecimalSuggestionDetailValue(directory, data, used)
                SuggestionDetail.TaxRate(value)
            }
    }

    private fun createCosts(
        directory: UID,
        autocompletes: List<Api15AutocompleteEntity>
    ): List<SuggestionDetail.Cost> {
        val filtered = autocompletes.filter { it.total > 0f }
        return filtered
            .distinctBy {getMoneyDecimalFormat().format(it.total.toBigDecimal()) }
            .map { autocomplete ->
                val data = autocomplete.total
                val used = filtered.count { it.total == autocomplete.total }
                val value = createDecimalSuggestionDetailValue(directory, data, used)
                SuggestionDetail.Cost(value)
            }
    }

    private fun createStringSuggestionDetailValue(
        directory: UID,
        data: String,
        used: Int
    ): SuggestionDetailValue<String> {
        val currentDateTime = DateTimeAlias.getCurrent()
        return SuggestionDetailValue(
            uid = UID.createRandom(),
            directory = directory,
            created = currentDateTime,
            lastModified = currentDateTime,
            data = data,
            used = used
        )
    }

    private fun createDecimalSuggestionDetailValue(
        directory: UID,
        data: Float,
        used: Int
    ): SuggestionDetailValue<Decimal> {
        val currentDateTime = DateTimeAlias.getCurrent()
        return SuggestionDetailValue(
            uid = UID.createRandom(),
            directory = directory,
            created = currentDateTime,
            lastModified = currentDateTime,
            data = data.toDecimal(),
            used = used
        )
    }

    private fun<T> createDecimalWithParamsSuggestionDetailValue(
        directory: UID,
        data: Float,
        params: T,
        used: Int
    ): SuggestionDetailValue<DecimalWithParams<T>> {
        val currentDateTime = DateTimeAlias.getCurrent()
        val data = DecimalWithParams(data.toDecimal(), params)
        return SuggestionDetailValue(
            uid = UID.createRandom(),
            directory = directory,
            created = currentDateTime,
            lastModified = currentDateTime,
            data = data,
            used = used
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