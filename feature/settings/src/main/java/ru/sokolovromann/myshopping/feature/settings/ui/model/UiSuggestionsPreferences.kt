package ru.sokolovromann.myshopping.feature.settings.ui.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.SuggestionAddingMode
import ru.sokolovromann.myshopping.core.domain.model.UserPreferencesDefaults

@Immutable
data class UiSuggestionsPreferences(
    val addingMode: UiPreference<SuggestionAddingMode> = UiPreference(UserPreferencesDefaults.Suggestions.addingMode)
)