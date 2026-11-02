package ru.sokolovromann.myshopping.feature.dictionary.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.ui.model.UiText

@Immutable
data class SuggestionItem(
    val uid: UID,
    val name: UiText,
    val fabrics: Map<UiText, String>
)