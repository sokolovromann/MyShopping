package ru.sokolovromann.myshopping.feature.settings.ui.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.SuggestionAddingMode
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsPreferences
import ru.sokolovromann.myshopping.core.ui.model.UiText
import ru.sokolovromann.myshopping.feature.settings.R
import ru.sokolovromann.myshopping.feature.settings.ui.model.UiPreference
import ru.sokolovromann.myshopping.feature.settings.ui.model.UiSuggestionsPreferences

class UiSuggestionsPreferencesMapper @Inject constructor() {

    fun toPreferences(preferences: SuggestionsPreferences): UiSuggestionsPreferences {
        val addingMode = UiPreference(
            selected = preferences.addingMode,
            text = when (preferences.addingMode) {
                SuggestionAddingMode.All ->
                    UiText.FromResources(R.string.settings_text_suggestion_adding_mode_all)
                SuggestionAddingMode.Name ->
                    UiText.FromResources(R.string.settings_text_suggestion_adding_mode_name)
                SuggestionAddingMode.Off ->
                    UiText.FromResources(R.string.settings_text_suggestion_adding_mode_off)
            }
        )
        return UiSuggestionsPreferences(addingMode)
    }
}