package ru.sokolovromann.myshopping.core.ui.extension

import androidx.compose.ui.graphics.vector.ImageVector
import ru.sokolovromann.myshopping.core.ui.model.UiIcon
import ru.sokolovromann.myshopping.core.ui.model.UiText

fun ImageVector.toUiIcon(): UiIcon = UiIcon.FromVector(this)

fun Int.toUiIcon(): UiIcon = UiIcon.FromResources(this)

fun String.toUiText(): UiText = UiText.FromString(this)

fun Int.toUiText(): UiText = UiText.FromResources(this)