package ru.sokolovromann.myshopping.feature.settings.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.DisplaySuggestionDetails
import ru.sokolovromann.myshopping.core.domain.model.DisplaySuggestionNames
import ru.sokolovromann.myshopping.core.domain.model.SuggestionAddingMode
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsPreferences
import ru.sokolovromann.myshopping.core.ui.model.UiText
import ru.sokolovromann.myshopping.feature.settings.R
import ru.sokolovromann.myshopping.feature.settings.model.SettingsItem
import ru.sokolovromann.myshopping.feature.settings.model.SuggestionsSettingsItems

class SuggestionsSettingsMapper @Inject constructor() {

    fun toItems(preferences: SuggestionsPreferences): SuggestionsSettingsItems {
        val addingMode = SettingsItem(
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
        val displayNames = SettingsItem(
            selected = preferences.displaySuggestionNames,
            text = when (preferences.displaySuggestionNames) {
                DisplaySuggestionNames.Low ->
                    UiText.FromResources(R.string.settings_text_display_suggestion_names_low)
                DisplaySuggestionNames.Medium ->
                    UiText.FromResources(R.string.settings_text_display_suggestion_names_medium)
                DisplaySuggestionNames.DoNotDisplay ->
                    UiText.FromResources(R.string.settings_text_display_suggestion_names_do_not_display)
            }
        )
        val displayDetails = SettingsItem(
            selected = preferences.displaySuggestionDetails,
            text = when (preferences.displaySuggestionDetails) {
                DisplaySuggestionDetails.Low ->
                    UiText.FromResources(R.string.settings_text_display_suggestion_details_low)
                DisplaySuggestionDetails.Medium ->
                    UiText.FromResources(R.string.settings_text_display_suggestion_details_medium)
                DisplaySuggestionDetails.Many ->
                    UiText.FromResources(R.string.settings_text_display_suggestion_details_many)
                DisplaySuggestionDetails.All ->
                    UiText.FromResources(R.string.settings_text_display_suggestion_details_all)
                DisplaySuggestionDetails.DoNotDisplay ->
                    UiText.FromResources(R.string.settings_text_display_suggestion_details_do_not_display)
            }
        )
        return SuggestionsSettingsItems(
            addingMode = addingMode,
            displayNames = displayNames,
            displayDetails = displayDetails
        )
    }
}