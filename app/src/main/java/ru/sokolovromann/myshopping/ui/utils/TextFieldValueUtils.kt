package ru.sokolovromann.myshopping.ui.utils

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.Quantity

fun TextFieldValue.isEmpty(): Boolean {
    return text.isEmpty()
}

fun TextFieldValue.isNotEmpty(): Boolean {
    return text.isNotEmpty()
}

fun TextFieldValue.toFloatOrZero(): Float {
    return text.toFloatOrNull() ?: 0f
}

fun TextFieldValue.toFloatOrNull(): Float? {
    return text.toFloatOrNull()
}

fun String.toTextFieldValue(): TextFieldValue {
    return TextFieldValue(
        text = this,
        selection = TextRange(this.length),
        composition = TextRange(this.length)
    )
}

fun Money.toTextFieldValue(displayZeroIfEmpty: Boolean = false): TextFieldValue {
    val text = if (isEmpty()) {
        if (displayZeroIfEmpty) {
            getFormattedValueWithoutSeparators()
        } else {
            ""
        }
    } else {
        getFormattedValueWithoutSeparators()
    }
    return TextFieldValue(
        text = text,
        selection = TextRange(text.length),
        composition = TextRange(text.length)
    )
}

fun Quantity.toTextFieldValue(): TextFieldValue {
    val text = if (isEmpty()) "" else getFormattedValueWithoutSeparators()
    return TextFieldValue(
        text = text,
        selection = TextRange(text.length),
        composition = TextRange(text.length)
    )
}