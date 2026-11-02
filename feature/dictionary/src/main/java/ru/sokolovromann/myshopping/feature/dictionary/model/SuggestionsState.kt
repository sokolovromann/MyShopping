package ru.sokolovromann.myshopping.feature.dictionary.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.UID

@Immutable
data class SuggestionsState(
    val isLoading: Boolean = true,
    val items: List<SuggestionItem> = emptyList(),
    val itemsCells: Int = 1,
    val itemsSelected: Set<UID> = emptySet()
)