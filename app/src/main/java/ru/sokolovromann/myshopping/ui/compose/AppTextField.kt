package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    valueColor: Color = MaterialTheme.colors.onSurface,
    valueFontSize: TextUnit = TextStyle.Default.fontSize,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    error: @Composable (() -> Unit)? = null,
    showError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE
) {
    val textStyle = createAppTextFieldTextStyle(valueColor, valueFontSize)

    AppTextFieldImpl(
        modifier = modifier,
        textStyle = textStyle,
        error = error,
        showError = showError
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            textStyle = textStyle,
            label = createAppTextLabelOrNot(textStyle, label),
            isError = showError,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines
        )
    }
}

@Composable
fun OutlinedAppTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    valueColor: Color = MaterialTheme.colors.onSurface,
    valueFontSize: TextUnit = TextStyle.Default.fontSize,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    error: @Composable (() -> Unit)? = null,
    showError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE
) {
    val textStyle = createAppTextFieldTextStyle(valueColor, valueFontSize)

    AppTextFieldImpl(
        modifier = modifier,
        textStyle = textStyle,
        error = error,
        showError = showError
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            textStyle = textStyle,
            label = createAppTextLabelOrNot(textStyle, label),
            isError = showError,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines
        )
    }
}

@Composable
private fun AppTextFieldImpl(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    error: @Composable (() -> Unit)? = null,
    showError: Boolean = false,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        content()

        if (error != null && showError) {
            Column(
                modifier = Modifier.padding(AppTextFieldErrorPaddings),
                content = { ProvideAppTextFieldErrorTextStyle(textStyle, error) }
            )
        }
    }
}

@Composable
private fun ProvideAppTextFieldLabelTextStyle(
    textStyle: TextStyle,
    content: @Composable () -> Unit
) {
    ProvideTextStyle(
        value = textStyle,
        content = content
    )
}

@Composable
private fun ProvideAppTextFieldErrorTextStyle(
    textStyle: TextStyle,
    content: @Composable () -> Unit
) {
    ProvideTextStyle(
        value = textStyle.copy(color = MaterialTheme.colors.error),
        content = content
    )
}

@Composable
private fun createAppTextLabelOrNot(
    textStyle: TextStyle,
    content: @Composable (() -> Unit)?
): @Composable (() -> Unit)? {
    return if (content == null) {
        null
    } else {
        { ProvideAppTextFieldLabelTextStyle(textStyle, content) }
    }
}

@Composable
private fun createAppTextFieldTextStyle(color: Color, fontSize: TextUnit): TextStyle {
    return TextStyle(color = color, fontSize = fontSize)
}

private val AppTextFieldErrorPaddings = PaddingValues(horizontal = 16.dp)