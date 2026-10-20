package ru.sokolovromann.myshopping.feature.settings.action

import ru.sokolovromann.myshopping.core.domain.model.DisplaySuggestionDetails
import ru.sokolovromann.myshopping.core.domain.model.DisplaySuggestionNames
import ru.sokolovromann.myshopping.core.domain.model.SuggestionAddingMode

sealed class SuggestionsSettingsAction {

    data class OnAddingModeSelected(val addingMode: SuggestionAddingMode) : SuggestionsSettingsAction()

    data class OnDisplayNamesSelected(val displayNames: DisplaySuggestionNames) : SuggestionsSettingsAction()

    data class OnDisplayDetailsSelected(val displayDetails: DisplaySuggestionDetails) : SuggestionsSettingsAction()
}