package ru.sokolovromann.myshopping.ui.utils

import androidx.compose.ui.text.input.TextFieldValue

fun TextFieldValue.isEmpty(): Boolean {
    return text.isEmpty()
}

fun TextFieldValue.isNotEmpty(): Boolean {
    return text.isNotEmpty()
}

fun TextFieldValue.toFloatOrZero(): Float {
    return text.toFloatOrNull() ?: 0f
}