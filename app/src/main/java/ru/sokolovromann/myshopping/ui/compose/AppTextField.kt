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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.ui.utils.isEmpty
import ru.sokolovromann.myshopping.ui.utils.toFloatOrNull

@Composable
fun OutlinedAppTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    valueColor: Color = MaterialTheme.colors.onSurface,
    valueFontSize: TextUnit = LocalTextStyle.current.fontSize,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    error: @Composable (() -> Unit)? = null,
    showError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        disabledBorderColor =  MaterialTheme.colors.onSurface.copy(alpha = 0.05f)
    )
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
            onValueChange = {
                val textFieldValue = checkTextFieldValue(
                    oldValue = value,
                    newValue = it,
                    keyboardType = keyboardOptions.keyboardType
                )
                onValueChange(textFieldValue)
            },
            enabled = enabled,
            textStyle = textStyle,
            label = createAppTextLabelOrNot(textStyle, label),
            trailingIcon = trailingIcon,
            isError = showError,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            colors = colors
        )
    }
}

private fun checkTextFieldValue(
    oldValue: TextFieldValue,
    newValue: TextFieldValue,
    keyboardType: KeyboardType
): TextFieldValue {
    return when (keyboardType) {
        KeyboardType.Decimal, KeyboardType.Number -> {
            val textFieldValue = TextFieldValue(
                text = newValue.text.replace(",", "."),
                selection = newValue.selection,
                composition = newValue.composition
            )
            if (textFieldValue.toFloatOrNull() == null) {
                if (textFieldValue.isEmpty()) textFieldValue else oldValue
            } else {
                textFieldValue
            }
        }
        else -> newValue
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