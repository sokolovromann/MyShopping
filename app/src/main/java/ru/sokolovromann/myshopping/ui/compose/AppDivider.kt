package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppHorizontalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface.copy(alpha = AppDividerAlpha),
    thickness: Dp = AppDividerThickness,
) {
    Divider(
        modifier = modifier,
        color = color,
        thickness = thickness
    )
}

@Composable
fun AppVerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface.copy(alpha = AppDividerAlpha),
    height: Dp = AppDividerHeight,
    thickness: Dp = AppDividerThickness,
) {
    Box(modifier = Modifier
        .height(height)
        .width(thickness)
        .background(color = color)
        .then(modifier)
    )
}

private const val AppDividerAlpha = 0.12f
private val AppDividerThickness = 1.dp
private val AppDividerHeight = 48.dp