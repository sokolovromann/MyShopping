package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.model.UiFontSize
import ru.sokolovromann.myshopping.ui.model.UiString

@Composable
fun AppTextGridHeader(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: UiFontSize
) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier.padding(AppTextGridHeaderPaddings),
            text = text,
            color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium),
            style = MaterialTheme.typography.body1,
            fontSize = fontSize.itemTitle.sp
        )
    }
}

@Composable
fun AppTextGridHeader(
    modifier: Modifier = Modifier,
    text: UiString,
    fontSize: UiFontSize
) {
    AppTextGridHeader(
        modifier = modifier,
        text = text.asCompose(),
        fontSize = fontSize
    )
}

@Composable
fun AppTopBarActionText(
    modifier: Modifier = Modifier,
    text: UiString,
    color: Color = DefaultAppTopBarText,
    fontSize: TextUnit = DefaultAppTopBarFontSize
) {
    Text(
        modifier = modifier,
        text = text.asCompose().uppercase(),
        color = color,
        fontSize = fontSize
    )
}

@Composable
fun SaveDataText(
    modifier: Modifier = Modifier,
    text: UiString = UiString.FromResources(R.string.all_action_save),
    color: Color = DefaultAppTopBarText,
    fontSize: TextUnit = DefaultAppTopBarFontSize
) {
    AppTopBarActionText(
        modifier = modifier,
        text = text,
        color = color,
        fontSize = fontSize
    )
}

private val AppTextGridHeaderPaddings = PaddingValues(
    horizontal = 16.dp,
    vertical = 8.dp
)

private val DefaultAppTopBarText: Color
    get() = Color.Unspecified

private val DefaultAppTopBarFontSize: TextUnit
    get() = TextUnit.Unspecified