package ru.sokolovromann.myshopping.feature.settings.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.DisplaySuggestionDetails
import ru.sokolovromann.myshopping.core.domain.model.DisplaySuggestionNames
import ru.sokolovromann.myshopping.core.domain.model.SuggestionAddingMode
import ru.sokolovromann.myshopping.core.domain.model.UserPreferencesDefaults

@Immutable
data class SuggestionsSettingsItems(
    val addingMode: SettingsItem<SuggestionAddingMode> = SettingsItem(UserPreferencesDefaults.Suggestions.addingMode),
    val displayNames: SettingsItem<DisplaySuggestionNames> = SettingsItem(UserPreferencesDefaults.Suggestions.displaySuggestionNames),
    val displayDetails: SettingsItem<DisplaySuggestionDetails> = SettingsItem(UserPreferencesDefaults.Suggestions.displaySuggestionDetails),
)