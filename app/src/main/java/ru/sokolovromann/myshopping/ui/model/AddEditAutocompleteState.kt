package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.data.model.UserPreferences
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetails
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionWithDetails
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsConfig
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsDefaults
import ru.sokolovromann.myshopping.data39.suggestions.TakeSuggestionDetails
import ru.sokolovromann.myshopping.ui.model.mapper.UiAutocompletesMapper
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue
import ru.sokolovromann.myshopping.utils.UID

class AddEditAutocompleteState {

    private lateinit var originalDetails: SuggestionDetails

    private var originalUserPreferences: UserPreferences = UserPreferences()

    private var takeDetails: TakeSuggestionDetails = SuggestionsDefaults.TAKE_DETAILS

    var nameValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var nameError: Boolean by mutableStateOf(false)
        private set

    var brands: Collection<Pair<String, UID>> by mutableStateOf(listOf())
        private set

    var sizes: Collection<Pair<String, UID>> by mutableStateOf(listOf())
        private set

    var colors: Collection<Pair<String, UID>> by mutableStateOf(listOf())
        private set

    var manufacturers: Collection<Pair<String, UID>> by mutableStateOf(listOf())
        private set

    var displayOtherFields: Boolean by mutableStateOf(false)
        private set

    var quantities: Collection<Pair<String, UID>> by mutableStateOf(listOf())
        private set

    var displayMoney: Boolean by mutableStateOf(true)
        private set

    var prices: Collection<Pair<String, UID>> by mutableStateOf(listOf())
        private set

    var discounts: Collection<Pair<String, UID>> by mutableStateOf(listOf())
        private set

    var totals: Collection<Pair<String, UID>> by mutableStateOf(listOf())
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    var deletedDetailsUids: MutableCollection<UID> = mutableListOf()
        private set

    fun populate(
        suggestionWithDetails: SuggestionWithDetails,
        userPreferences: UserPreferences,
        suggestionsConfig: SuggestionsConfig
    ) {
        val details = suggestionWithDetails.details
        val currency = userPreferences.currency

        originalDetails = details
        originalUserPreferences = userPreferences
        takeDetails = suggestionsConfig.takeDetails
        nameValue = suggestionWithDetails.suggestion.name.toTextFieldValue()
        nameError = false
        brands = UiAutocompletesMapper.toUiBrandsWithUids(details.brands).takeDetails()
        sizes = UiAutocompletesMapper.toUiSizesWithUids(details.sizes).takeDetails()
        colors = UiAutocompletesMapper.toUiColorsWithUids(details.colors).takeDetails()
        manufacturers = UiAutocompletesMapper.toUiManufacturersWithUids(details.manufacturers).takeDetails()
        displayOtherFields = userPreferences.displayOtherFields
        quantities = UiAutocompletesMapper.toUiQuantitiesWithUids(details.quantities, userPreferences.quantityDecimalFormat).takeDetails()
        displayMoney = userPreferences.displayMoney
        prices = UiAutocompletesMapper.toUiPricesWithUids(details.unitPrices, currency, userPreferences.moneyDecimalFormat).takeDetails()
        discounts = UiAutocompletesMapper.toUiDiscountsWithUids(details.discounts, currency, userPreferences.moneyDecimalFormat).takeDetails()
        totals = UiAutocompletesMapper.toUiTotalsWithUids(details.costs, currency, userPreferences.moneyDecimalFormat).takeDetails()
        waiting = false
    }

    fun onNameValueChanged(value: TextFieldValue) {
        nameValue = value
        nameError = false
        waiting = false
    }

    fun onInvalidNameValue() {
        nameError = true
        waiting = false
    }

    fun onDetailDeleted(uid: UID, type: String) {
        deletedDetailsUids.add(uid)

        fun Collection<Pair<String, UID>>.withoutDeleted(): Collection<Pair<String, UID>> {
            return toMutableList().apply {
                removeIf { value ->
                    deletedDetailsUids.find { it == value.second } != null
                }
            }
        }
        when (type) {
            "brand" -> {
                brands = UiAutocompletesMapper.toUiBrandsWithUids(
                    originalDetails.brands
                ).withoutDeleted().takeDetails()
            }
            "size" -> {
                sizes = UiAutocompletesMapper.toUiSizesWithUids(
                    originalDetails.sizes
                ).withoutDeleted().takeDetails()
            }
            "color" -> {
                colors = UiAutocompletesMapper.toUiColorsWithUids(
                    originalDetails.colors
                ).withoutDeleted().takeDetails()
            }
            "manufacturer" -> {
                manufacturers = UiAutocompletesMapper.toUiManufacturersWithUids(
                    originalDetails.manufacturers
                ).withoutDeleted().takeDetails()
            }
            "quantity" -> {
                quantities = UiAutocompletesMapper.toUiQuantitiesWithUids(
                    originalDetails.quantities,
                    originalUserPreferences.quantityDecimalFormat
                ).withoutDeleted().takeDetails()
            }
            "price" -> {
                prices = UiAutocompletesMapper.toUiPricesWithUids(
                    originalDetails.unitPrices,
                    originalUserPreferences.currency,
                    originalUserPreferences.moneyDecimalFormat
                ).withoutDeleted().takeDetails()
            }
            "discount" -> {
                discounts = UiAutocompletesMapper.toUiDiscountsWithUids(
                    originalDetails.discounts,
                    originalUserPreferences.currency,
                    originalUserPreferences.moneyDecimalFormat
                ).withoutDeleted().takeDetails()
            }
            "total" -> {
                totals = UiAutocompletesMapper.toUiTotalsWithUids(
                    originalDetails.costs,
                    originalUserPreferences.currency,
                    originalUserPreferences.moneyDecimalFormat
                ).withoutDeleted().takeDetails()
            }
            else -> {}
        }
    }

    fun onClearDeletedDetailsUids() {
        deletedDetailsUids.clear()
    }

    fun onWaiting() {
        waiting = true
    }

    private fun <T> Iterable<T>.takeDetails(): List<T> {
        return when (takeDetails) {
            TakeSuggestionDetails.All -> this
            TakeSuggestionDetails.Few -> this.take(3)
            TakeSuggestionDetails.Medium -> this.take(5)
            TakeSuggestionDetails.Many -> this.take(10)
            TakeSuggestionDetails.DoNotTake -> emptyList()
        }.toList()
    }
}