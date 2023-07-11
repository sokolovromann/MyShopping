package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import ru.sokolovromann.myshopping.ui.utils.toItemTitle

@Composable
fun AppTextGridHeader(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: FontSize
) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier.padding(AppTextGridHeaderPaddings),
            text = text,
            color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium),
            style = MaterialTheme.typography.body1,
            fontSize = fontSize.toItemTitle().sp
        )
    }
}

@Composable
fun AppTextGridHeader(
    modifier: Modifier = Modifier,
    text: UiText,
    fontSize: FontSize
) {
    AppTextGridHeader(
        modifier = modifier,
        text = text.asCompose(),
        fontSize = fontSize
    )
}

private val AppTextGridHeaderPaddings = PaddingValues(
    horizontal = 16.dp,
    vertical = 8.dp
)