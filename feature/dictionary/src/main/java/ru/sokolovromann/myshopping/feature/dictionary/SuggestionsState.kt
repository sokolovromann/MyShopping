package ru.sokolovromann.myshopping.feature.dictionary

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.UID

@Immutable
data class SuggestionsState(
    val items: List<SuggestionItem> = emptyList(),
    val cells: Int = 1,
    val selected: Set<UID> = emptySet()
) {

    val isNotFound: Boolean get() = items.isEmpty()

    val selectedCount: Int get() = selected.count()

    val isSelectMode: Boolean get() = selected.count() > 0

    val isSingleSelectMode: Boolean get() = selected.count() == 1

    val isMultiSelectMode: Boolean get() = selected.count() > 1
}