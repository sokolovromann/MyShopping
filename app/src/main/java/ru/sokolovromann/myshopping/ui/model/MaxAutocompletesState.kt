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

    var takeSuggestionsValue: SelectedValue<TakeSuggestions> by mutableStateOf(
        SelectedValue(SuggestionsDefaults.TAKE_SUGGESTIONS)
    )

    var takeDetailsValue: SelectedValue<TakeSuggestionDetails> by mutableStateOf(
        SelectedValue(SuggestionsDefaults.TAKE_DETAILS)
    )

    var expandedTakeSuggestions: Boolean by mutableStateOf(false)
        private set

    var expandedTakeDetails: Boolean by mutableStateOf(false)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(suggestionsConfig: SuggestionsConfig) {
        takeSuggestionsValue = toTakeSuggestionsValue(suggestionsConfig.takeSuggestions)
        takeDetailsValue = toTakeDetailsValue(suggestionsConfig.takeDetails)
        expandedTakeSuggestions = false
        expandedTakeDetails = false
        waiting = false
    }

    fun onSelectTakeSuggestions(expanded: Boolean) {
        expandedTakeSuggestions = expanded
    }

    fun onSelectTakeDetails(expanded: Boolean) {
        expandedTakeDetails = expanded
    }

    fun onTakeSuggestionsSelected(takeSuggestions: TakeSuggestions) {
        takeSuggestionsValue = toTakeSuggestionsValue(takeSuggestions)
        expandedTakeSuggestions = false
    }

    fun onTakeDetailsSelected(takeSuggestionDetails: TakeSuggestionDetails) {
        takeDetailsValue = toTakeDetailsValue(takeSuggestionDetails)
        expandedTakeDetails = false
    }

    fun onWaiting() {
        waiting = true
    }

    private fun toTakeSuggestionsValue(takeSuggestions: TakeSuggestions): SelectedValue<TakeSuggestions> {
        return SelectedValue(
            selected = takeSuggestions,
            text = when (takeSuggestions) {
                TakeSuggestions.Few -> UiString.FromResources(R.string.maxAutocompletes_body_takeFewSuggestions)
                TakeSuggestions.Medium -> UiString.FromResources(R.string.maxAutocompletes_body_takeMediumSuggestions)
                TakeSuggestions.DoNotTake -> UiString.FromResources(R.string.maxAutocompletes_body_doNotTakeSuggestions)
            }
        )
    }

    private fun toTakeDetailsValue(takeSuggestionDetails: TakeSuggestionDetails): SelectedValue<TakeSuggestionDetails> {
        return SelectedValue(
            selected = takeSuggestionDetails,
            text = when (takeSuggestionDetails) {
                TakeSuggestionDetails.All -> UiString.FromResources(R.string.maxAutocompletes_body_takeAllDetails)
                TakeSuggestionDetails.Few -> UiString.FromResources(R.string.maxAutocompletes_body_takeFewDetails)
                TakeSuggestionDetails.Medium -> UiString.FromResources(R.string.maxAutocompletes_body_takeMediumDetails)
                TakeSuggestionDetails.Many -> UiString.FromResources(R.string.maxAutocompletes_body_takeManyDetails)
                TakeSuggestionDetails.DoNotTake -> UiString.FromResources(R.string.maxAutocompletes_body_doNotTakeDetails)
            }
        )
    }
}