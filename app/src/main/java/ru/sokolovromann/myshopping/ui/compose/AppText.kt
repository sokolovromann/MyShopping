package ru.sokolovromann.myshopping.ui.compose

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.ui.compose.state.TextData
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import ru.sokolovromann.myshopping.ui.theme.MyShoppingTheme

@Composable
fun AppText(modifier: Modifier = Modifier, data: TextData) {
    if (data.isTextHiding()) {
        return
    }

    Text(
        modifier = modifier,
        text = data.text.asCompose(),
        style = data.style,
        color = data.color.asCompose(),
        fontSize = data.fontSize,
        fontWeight = data.fontWeight,
        overflow = data.overflow,
        maxLines = data.maxLines
    )
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AppTextPreview() {
    MyShoppingTheme {
        Surface {
            Column {
                AppText(
                    modifier = Modifier.padding(all = 8.dp),
                    data = TextData.Header.copy(text = UiText.FromString("Header ".repeat(10)))
                )
                AppText(
                    modifier = Modifier.padding(all = 8.dp),
                    data = TextData.Title.copy(text = UiText.FromString("Title ".repeat(10)))
                )
                AppText(
                    modifier = Modifier.padding(all = 8.dp),
                    data = TextData.Body.copy(text = UiText.FromString("Body ".repeat(20)))
                )
                AppText(
                    modifier = Modifier.padding(all = 8.dp),
                    data = TextData.Body.copy(text = UiText.Nothing)
                )
            }
        }
    }
}