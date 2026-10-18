package ru.sokolovromann.myshopping.feature.settings.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.ui.model.UiText

@Immutable
data class SettingsItem<S>(
    val selected: S,
    val text: UiText = UiText.FromString("")
)