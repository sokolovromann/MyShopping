package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsConfig
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsDefaults
import ru.sokolovromann.myshopping.data39.suggestions.TakeSuggestionDetails
import ru.sokolovromann.myshopping.data39.suggestions.TakeSuggestions

class MaxAutocompletesState {

    var takeNamesValue: SelectedValue<TakeSuggestions> by mutableStateOf(
        SelectedValue(SuggestionsDefaults.TAKE_SUGGESTIONS)
    )

    var takeDetailsDescriptions: SelectedValue<TakeSuggestionDetails> by mutableStateOf(
        SelectedValue(SuggestionsDefaults.TAKE_DESCRIPTIONS)
    )

    var takeDetailsQuantities: SelectedValue<TakeSuggestionDetails> by mutableStateOf(
        SelectedValue(SuggestionsDefaults.TAKE_QUANTITIES)
    )

    var takeDetailsMoney: SelectedValue<TakeSuggestionDetails> by mutableStateOf(
        SelectedValue(SuggestionsDefaults.TAKE_MONEY)
    )

    var expandedTakeNames: Boolean by mutableStateOf(false)
        private set

    var expandedTakeDetailsDescriptions: Boolean by mutableStateOf(false)
        private set

    var expandedTakeDetailsQuantities: Boolean by mutableStateOf(false)
        private set

    var expandedTakeDetailsMoney: Boolean by mutableStateOf(false)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(suggestionsConfig: SuggestionsConfig) {
        takeNamesValue = toTakeNamesValue(suggestionsConfig.takeSuggestions)
        takeDetailsDescriptions = toTakeDetailsDescriptions(suggestionsConfig.takeDetails.descriptions)
        takeDetailsQuantities = toTakeDetailsDescriptions(suggestionsConfig.takeDetails.quantities)
        takeDetailsMoney = toTakeDetailsDescriptions(suggestionsConfig.takeDetails.money)
        expandedTakeNames = false
        expandedTakeDetailsDescriptions = false
        expandedTakeDetailsQuantities = false
        expandedTakeDetailsMoney = false
        waiting = false
    }

    fun onSelectTakeNames(expanded: Boolean) {
        expandedTakeNames = expanded
    }

    fun onSelectTakeDetailsDescriptions(expanded: Boolean) {
        expandedTakeDetailsDescriptions = expanded
    }

    fun onSelectTakeDetailsQuantities(expanded: Boolean) {
        expandedTakeDetailsQuantities = expanded
    }

    fun onSelectTakeDetailsMoney(expanded: Boolean) {
        expandedTakeDetailsMoney = expanded
    }

    fun onTakeNamesSelected(takeSuggestions: TakeSuggestions) {
        takeNamesValue = toTakeNamesValue(takeSuggestions)
        expandedTakeNames = false
    }

    fun onTakeDetailsDescriptionsSelected(takeSuggestionDetails: TakeSuggestionDetails) {
        takeDetailsDescriptions = toTakeDetailsDescriptions(takeSuggestionDetails)
        expandedTakeDetailsDescriptions = false
    }

    fun onTakeDetailsQuantitiesSelected(takeSuggestionDetails: TakeSuggestionDetails) {
        takeDetailsQuantities = toTakeDetailsDescriptions(takeSuggestionDetails)
        expandedTakeDetailsQuantities = false
    }

    fun onTakeDetailsMoneySelected(takeSuggestionDetails: TakeSuggestionDetails) {
        takeDetailsMoney = toTakeDetailsDescriptions(takeSuggestionDetails)
        expandedTakeDetailsMoney = false
    }


    fun onWaiting() {
        waiting = true
    }

    private fun toTakeNamesValue(takeSuggestions: TakeSuggestions): SelectedValue<TakeSuggestions> {
        return SelectedValue(
            selected = takeSuggestions,
            text = when (takeSuggestions) {
                TakeSuggestions.Five -> UiString.FromResources(R.string.maxAutocompletes_body_takeFiveSuggestions)
                TakeSuggestions.Ten -> UiString.FromResources(R.string.maxAutocompletes_body_takeTenSuggestions)
                TakeSuggestions.DoNotTake -> UiString.FromResources(R.string.maxAutocompletes_body_doNotTakeSuggestions)
            }
        )
    }

    private fun toTakeDetailsDescriptions(takeSuggestionDetails: TakeSuggestionDetails): SelectedValue<TakeSuggestionDetails> {
        return SelectedValue(
            selected = takeSuggestionDetails,
            text = when (takeSuggestionDetails) {
                TakeSuggestionDetails.All -> UiString.FromResources(R.string.maxAutocompletes_body_takeAllDetails)
                TakeSuggestionDetails.One -> UiString.FromResources(R.string.maxAutocompletes_body_takeOneDetails)
                TakeSuggestionDetails.Three -> UiString.FromResources(R.string.maxAutocompletes_body_takeThreeDetails)
                TakeSuggestionDetails.Five -> UiString.FromResources(R.string.maxAutocompletes_body_takeFiveDetails)
                TakeSuggestionDetails.Ten -> UiString.FromResources(R.string.maxAutocompletes_body_takeTenDetails)
                TakeSuggestionDetails.DoNotTake -> UiString.FromResources(R.string.maxAutocompletes_body_doNotTakeDetails)
            }
        )
    }
}