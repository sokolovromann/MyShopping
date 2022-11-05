package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import ru.sokolovromann.myshopping.ui.theme.AppColor
import ru.sokolovromann.myshopping.ui.theme.AppTypography

class TextFieldState {

    var currentData by mutableStateOf(TextFieldData())
        private set

    fun showTextField(text: TextFieldValue, label: TextData, keyboardOptions: KeyboardOptions) {
        currentData = TextFieldData(
            text = text,
            label = label,
            keyboardOptions = keyboardOptions
        )
    }

    fun showError(error: TextData) {
        currentData = currentData.copy(error = error)
    }

    fun hideTextField() {
        currentData = currentData.copy(hideTextField = true)
    }

    fun changeText(text: TextFieldValue) {
        currentData = currentData.copy(
            text = text,
            error = null
        )
    }

    fun isTextEmpty(): Boolean {
        return currentData.text.text.isEmpty()
    }

    fun isTextNotEmpty(): Boolean {
        return currentData.text.text.isNotEmpty()
    }

    fun isError(): Boolean {
        return currentData.error != null
    }
}

data class TextFieldData(
    val text: TextFieldValue = TextFieldValue(),
    val textFontSize: TextUnit = AppTypography.Body2.textStyle.fontSize,
    val textColor: ColorData = ColorData(appColor = AppColor.OnSurface),
    val label: TextData = TextData(),
    val error: TextData? = null,
    val keyboardOptions: KeyboardOptions = KeyboardOptions(),
    val maxLines: Int = 1,
    val hideTextField: Boolean = false
)