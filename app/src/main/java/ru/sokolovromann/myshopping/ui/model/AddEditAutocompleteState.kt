package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.data.model.UserPreferences
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionWithDetails
import ru.sokolovromann.myshopping.ui.model.mapper.UiAutocompletesMapper
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue
import ru.sokolovromann.myshopping.utils.UID

class AddEditAutocompleteState {

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

    fun populate(suggestionWithDetails: SuggestionWithDetails, userPreferences: UserPreferences) {
        val details = suggestionWithDetails.details
        val currency = userPreferences.currency

        nameValue = suggestionWithDetails.suggestion.name.toTextFieldValue()
        nameError = false
        brands = UiAutocompletesMapper.toUiBrands(details.brands)
        sizes = UiAutocompletesMapper.toUiSizes(details.sizes)
        colors = UiAutocompletesMapper.toUiColors(details.colors)
        manufacturers = UiAutocompletesMapper.toUiManufacturers(details.manufacturers)
        displayOtherFields = userPreferences.displayOtherFields
        quantities = UiAutocompletesMapper.toUiQuantities(details.quantities)
        displayMoney = userPreferences.displayMoney
        prices = UiAutocompletesMapper.toUiPrices(details.unitPrices, currency)
        discounts = UiAutocompletesMapper.toUiDiscounts(details.discounts, currency)
        totals = UiAutocompletesMapper.toUiTotals(details.costs, currency)
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

    fun onWaiting() {
        waiting = true
    }
}