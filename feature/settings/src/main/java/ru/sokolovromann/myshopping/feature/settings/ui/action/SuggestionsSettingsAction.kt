package ru.sokolovromann.myshopping.feature.settings.ui.action

import ru.sokolovromann.myshopping.core.domain.model.SuggestionAddingMode

sealed class SuggestionsSettingsAction {

    data class OnAddingModeSelected(val addingMode: SuggestionAddingMode) : SuggestionsSettingsAction()
}