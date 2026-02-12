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
import ru.sokolovromann.myshopping.utils.calendar.DateTimeAlias
import ru.sokolovromann.myshopping.utils.math.Decimal
import ru.sokolovromann.myshopping.utils.math.DecimalExtensions.toDecimal
import ru.sokolovromann.myshopping.utils.math.DecimalWithParams
import ru.sokolovromann.myshopping.utils.math.DiscountType
import javax.inject.Inject

class MigrationManager @Inject constructor(
    private val api15Manager: Api15Manager,
    private val suggestionsManager: SuggestionsManager
) {

    suspend fun migrateApi15Autocompletes(): Unit = withIoContext {
        val suggestionsWithDetails = api15Manager.getAutocompletes()
            .mapKeys { (key, _) ->
                val used = api15Manager.countAutocompleteNames(key)
                toSuggestion(key, used)
            }
            .mapValues { (key, value) -> toDetails(key, value) }

        val suggestions = suggestionsWithDetails.keys
        suggestionsManager.addSuggestions(suggestions)

        val details = suggestionsWithDetails.flatMap { it.value }
        suggestionsManager.addDetails(details)
    }

    suspend fun migrateApi15AutocompletesConfig(): Unit = withIoContext {
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

    private fun toSuggestion(name: String, used: Int): Suggestion {
        val currentDateTime = DateTimeAlias.getCurrent()
        return Suggestion(
            uid = UID.createRandom(),
            directory = SuggestionDirectory.NoDirectory,
            created = currentDateTime,
            lastModified = currentDateTime,
            name = name,
            used = used
        )
    }

    private suspend fun toDetails(
        suggestion: Suggestion,
        autocompletes: List<Api15AutocompleteEntity>
    ): Collection<SuggestionDetail> = withIoContext {
        return@withIoContext mutableListOf<SuggestionDetail>().apply {
            val manufacturers = autocompletes.map {
                val used = api15Manager.countAutocompleteDetails(suggestion.name, "Manufacturer")
                val value = createStringSuggestionDetailValue(suggestion.uid, it.manufacturer, used)
                SuggestionDetail.Manufacturer(value)
            }
            addAll(manufacturers)

            val brands = autocompletes.map {
                val used = api15Manager.countAutocompleteDetails(suggestion.name, "Brand")
                val value = createStringSuggestionDetailValue(suggestion.uid, it.brand, used)
                SuggestionDetail.Brand(value)
            }
            addAll(brands)

            val sizes = autocompletes.map {
                val used = api15Manager.countAutocompleteDetails(suggestion.name, "Size")
                val value = createStringSuggestionDetailValue(suggestion.uid, it.size, used)
                SuggestionDetail.Size(value)
            }
            addAll(sizes)

            val colors = autocompletes.map {
                val used = api15Manager.countAutocompleteDetails(suggestion.name, "Color")
                val value = createStringSuggestionDetailValue(suggestion.uid, it.color, used)
                SuggestionDetail.Color(value)
            }
            addAll(colors)

            val quantities = autocompletes.map {
                val currentDateTime = DateTimeAlias.getCurrent()
                val used = api15Manager.countAutocompleteDetails(suggestion.name, "Quantities")
                val value = SuggestionDetailValue(
                    uid = UID.createRandom(),
                    directory = suggestion.uid,
                    created = currentDateTime,
                    lastModified = currentDateTime,
                    data = DecimalWithParams(it.quantity.toDecimal(), it.quantitySymbol),
                    used = used
                )
                SuggestionDetail.Quantity(value)
            }
            addAll(quantities)

            val unitPrices = autocompletes.map {
                val used = api15Manager.countAutocompleteDetails(suggestion.name, "UnitPrices")
                val value = createDecimalSuggestionDetailValue(suggestion.uid, it.price, used)
                SuggestionDetail.UnitPrice(value)
            }
            addAll(unitPrices)

            val discounts = autocompletes.map {
                val currentDateTime = DateTimeAlias.getCurrent()
                val type: DiscountType = if (it.discountAsPercent) {
                    DiscountType.Percent
                } else {
                    DiscountType.Money
                }
                val used = api15Manager.countAutocompleteDetails(suggestion.name, "Discounts")
                val value = SuggestionDetailValue(
                    uid = UID.createRandom(),
                    directory = suggestion.uid,
                    created = currentDateTime,
                    lastModified = currentDateTime,
                    data = DecimalWithParams(it.discount.toDecimal(), type),
                    used = used
                )
                SuggestionDetail.Discount(value)
            }
            addAll(discounts)

            val taxRates = autocompletes.map {
                val used = api15Manager.countAutocompleteDetails(suggestion.name, "TaxRates")
                val value = createDecimalSuggestionDetailValue(suggestion.uid, it.taxRate, used)
                SuggestionDetail.TaxRate(value)
            }
            addAll(taxRates)

            val costs = autocompletes.map {
                val used = api15Manager.countAutocompleteDetails(suggestion.name, "Costs")
                val value = createDecimalSuggestionDetailValue(suggestion.uid, it.total, used)
                SuggestionDetail.Cost(value)
            }
            addAll(costs)
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
}