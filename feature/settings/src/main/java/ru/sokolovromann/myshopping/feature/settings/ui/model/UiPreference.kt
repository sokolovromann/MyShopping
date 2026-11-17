package ru.sokolovromann.myshopping.feature.settings.ui.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.ui.model.UiText

@Immutable
data class UiPreference<D>(
    val selected: D,
    val text: UiText = UiText.FromString("")
)