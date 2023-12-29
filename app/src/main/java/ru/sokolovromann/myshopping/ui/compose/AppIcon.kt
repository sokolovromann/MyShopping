package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.sokolovromann.myshopping.ui.model.UiIcon

@Composable
fun AppDrawerContentItemIcon(
    modifier: Modifier = Modifier,
    icon: UiIcon,
    selected: Boolean,
    selectedTint: Color = MaterialTheme.colors.secondary,
    unselectedTint: Color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
) {
    val tint = if (selected) selectedTint else unselectedTint

    Icon(
        modifier = modifier,
        painter = icon.asPainter(),
        contentDescription = "",
        tint = tint
    )
}