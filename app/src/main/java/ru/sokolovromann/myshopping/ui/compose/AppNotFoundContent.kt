package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppNotFoundContent(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor),
    text: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .padding(AppNotFoundContentPaddings)
            .then(modifier)
    ) {
        ProvideTextStyle(
            value = MaterialTheme.typography.subtitle1.copy(
                color = contentColor
            ),
            content = text
        )
    }
}

private val AppNotFoundContentPaddings = PaddingValues(
    vertical = 8.dp,
    horizontal = 16.dp
)