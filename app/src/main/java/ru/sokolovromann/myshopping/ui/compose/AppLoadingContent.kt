package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppLoadingContent(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.background,
    indicator: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .padding(AppLoadingContentPaddings)
            .then(modifier),
        content = { indicator() }
    )
}

private val AppLoadingContentPaddings = PaddingValues(
    vertical = 8.dp,
    horizontal = 16.dp
)