package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data39.suggestions.TakeSuggestionDetails
import ru.sokolovromann.myshopping.data39.suggestions.TakeSuggestions

sealed class MaxAutocompletesEvent {

    object OnClickSave : MaxAutocompletesEvent()

    object OnClickCancel : MaxAutocompletesEvent()

    data class OnSelectTakeSuggestions(val expanded: Boolean) : MaxAutocompletesEvent()

    data class OnSelectTakeDetails(val expanded: Boolean) : MaxAutocompletesEvent()

    data class OnTakeSuggestionsSelected(val takeSuggestions: TakeSuggestions) : MaxAutocompletesEvent()

    data class OnTakeDetailsSelected(val takeSuggestionDetails: TakeSuggestionDetails) : MaxAutocompletesEvent()
}