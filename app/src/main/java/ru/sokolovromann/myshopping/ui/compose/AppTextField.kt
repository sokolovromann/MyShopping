package ru.sokolovromann.myshopping.ui.compose

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.ui.compose.state.TextData
import ru.sokolovromann.myshopping.ui.compose.state.TextFieldState
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import ru.sokolovromann.myshopping.ui.theme.MyShoppingTheme

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    onValueChange: (TextFieldValue) -> Unit
) {
    val data = state.currentData
    if (data.hideTextField) {
        return
    }

    Column(modifier = modifier) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = data.text,
            onValueChange = { onValueChange(it) },
            label = { AppText(data = data.label) },
            isError = state.isError(),
            keyboardOptions = data.keyboardOptions,
            maxLines = data.maxLines,
            textStyle = TextStyle.Default.copy(
                fontSize = data.textFontSize,
                color = data.textColor.asCompose()
            )
        )

        data.error?.let {
            AppText(
                modifier = Modifier.padding(horizontal = 16.dp),
                data = it
            )
        }
    }
}

@Composable
fun OutlinedAppTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    onValueChange: (TextFieldValue) -> Unit
) {
    val data = state.currentData
    if (data.hideTextField) {
        return
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = data.text,
            onValueChange = { onValueChange(it) },
            label = { AppText(data = data.label) },
            isError = state.isError(),
            keyboardOptions = data.keyboardOptions,
            maxLines = data.maxLines,
            textStyle = TextStyle.Default.copy(
                fontSize = data.textFontSize,
                color = data.textColor.asCompose()
            )
        )

        data.error?.let {
            AppText(
                modifier = Modifier.padding(horizontal = 16.dp),
                data = it
            )
        }
    }
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AppTextFieldPreview() {
    MyShoppingTheme {
        Surface {
            Column {
                AppTextField(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    state = TextFieldState().apply {
                        showTextField(
                            text = TextFieldValue("Description"),
                            label = TextData(text = UiText.FromString("Description")),
                            keyboardOptions = KeyboardOptions.Default
                        )
                    },
                    onValueChange = {}
                )

                AppTextField(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    state = TextFieldState().apply {
                        showTextField(
                            text = TextFieldValue("Error"),
                            label = TextData(text = UiText.FromString("Error")),
                            keyboardOptions = KeyboardOptions.Default
                        )
                        showError(TextData(text = UiText.FromString("Enter text")))
                    },
                    onValueChange = {}
                )

                OutlinedAppTextField(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    state = TextFieldState().apply {
                        showTextField(
                            text = TextFieldValue("Outlined ".repeat(5)),
                            label = TextData(text = UiText.FromString("Outlined")),
                            keyboardOptions = KeyboardOptions.Default
                        )
                    },
                    onValueChange = {}
                )
            }
        }
    }
}