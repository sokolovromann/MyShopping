package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data39.suggestions.TakeSuggestionDetails
import ru.sokolovromann.myshopping.data39.suggestions.TakeSuggestions

sealed class MaxAutocompletesEvent {

    object OnClickSave : MaxAutocompletesEvent()

    object OnClickCancel : MaxAutocompletesEvent()

    data class OnSelectTakeNames(val expanded: Boolean) : MaxAutocompletesEvent()

    data class OnSelectTakeDetailsDescriptions(val expanded: Boolean) : MaxAutocompletesEvent()

    data class OnSelectTakeDetailsQuantities(val expanded: Boolean) : MaxAutocompletesEvent()

    data class OnSelectTakeDetailsMoney(val expanded: Boolean) : MaxAutocompletesEvent()

    data class OnTakeNamesSelected(val takeSuggestions: TakeSuggestions) : MaxAutocompletesEvent()

    data class OnTakeDetailsDescriptionsSelected(val takeSuggestionDetails: TakeSuggestionDetails) : MaxAutocompletesEvent()

    data class OnTakeDetailsQuantitiesSelected(val takeSuggestionDetails: TakeSuggestionDetails) : MaxAutocompletesEvent()

    data class OnTakeDetailsMoneySelected(val takeSuggestionDetails: TakeSuggestionDetails) : MaxAutocompletesEvent()
}