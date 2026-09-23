package ru.sokolovromann.myshopping.feature.dictionary

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.ui.model.UiText

@Immutable
data class SuggestionItem(
    val uid: UID,
    val name: UiText,
    val quantities: UiText?,
    val unitPrices: UiText?,
    val discounts: UiText?,
    val taxes: UiText?,
    val costs: UiText?,
    val manufacturers: UiText?,
    val brands: UiText?,
    val sizes: UiText?,
    val colors: UiText?
)