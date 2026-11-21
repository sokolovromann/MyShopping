package ru.sokolovromann.myshopping.feature.dictionary.ui.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.SortSuggestions
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsView
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.model.UserPreferencesDefaults

@Immutable
data class SuggestionsState(
    val isProgress: Boolean = true,
    val suggestions: List<UiSuggestion> = emptyList(),
    val cells: Int = 1,
    val selected: Set<UID> = emptySet(),
    val sort: SortSuggestions = UserPreferencesDefaults.Suggestions.sort,
    val view: SuggestionsView = UserPreferencesDefaults.Suggestions.view
)