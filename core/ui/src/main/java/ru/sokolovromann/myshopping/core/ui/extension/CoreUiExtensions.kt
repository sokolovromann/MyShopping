package ru.sokolovromann.myshopping.core.ui.extension

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.core.ui.model.UiIcon
import ru.sokolovromann.myshopping.core.ui.model.UiText

fun Int.toUiIcon(): UiIcon = UiIcon.FromResources(this)

fun Int.toUiText(): UiText = UiText.FromResources(this)

fun String.toUiText(): UiText = UiText.FromString(this)

fun String.toTextFieldValue() = TextFieldValue(
    text = this,
    selection = TextRange(this.length),
    composition = TextRange(this.length)
)

fun ImageVector.toUiIcon(): UiIcon = UiIcon.FromVector(this)