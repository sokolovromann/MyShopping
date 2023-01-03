package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppChip(
    onClick: () -> Unit,
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .defaultMinSize(AppChipDefaultMinSize)
            .background(color = backgroundColor, shape = CircleShape)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(AppChipPaddings)) {
            ProvideTextStyle(
                value = MaterialTheme.typography.body2.copy(
                    color = contentColor
                ),
                content = { content() }
            )
        }
    }
}

private val AppChipDefaultMinSize = 32.dp
private val AppChipPaddings = PaddingValues(all = 8.dp)