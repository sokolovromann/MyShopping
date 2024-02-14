package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.model.UiIcon
import ru.sokolovromann.myshopping.ui.model.UiString

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

@Composable
fun AppTopBarIcon(
    icon: UiIcon,
    modifier: Modifier = Modifier,
    size: Dp = DefaultIconSize,
    contentDescription: UiString? = null,
    tint: Color = DefaultIconTint
) {
    Icon(
        modifier = Modifier
            .size(size)
            .then(modifier),
        painter = icon.asPainter(),
        contentDescription = contentDescription?.asCompose(),
        tint = tint
    )
}

@Composable
fun OpenNavigationIcon(
    modifier: Modifier = Modifier,
    size: Dp = DefaultIconSize,
    contentDescription: UiString? = UiString.FromResources(R.string.all_contentDescription_openNavigation),
    tint: Color = DefaultIconTint
) {
    AppTopBarIcon(
        icon = UiIcon.FromVector(Icons.Default.Menu),
        modifier = modifier,
        size = size,
        contentDescription = contentDescription,
        tint = tint
    )
}

@Composable
fun CancelSearchIcon(
    modifier: Modifier = Modifier,
    size: Dp = DefaultIconSize,
    contentDescription: UiString? = UiString.FromResources(R.string.all_contentDescription_cancelSearch),
    tint: Color = DefaultIconTint
) {
    AppTopBarIcon(
        icon = UiIcon.FromVector(Icons.Default.Clear),
        modifier = modifier,
        size = size,
        contentDescription = contentDescription,
        tint = tint
    )
}

@Composable
fun CancelSelectionIcon(
    modifier: Modifier = Modifier,
    size: Dp = DefaultIconSize,
    contentDescription: UiString? = UiString.FromResources(R.string.all_contentDescription_cancelSelection),
    tint: Color = DefaultIconTint
) {
    AppTopBarIcon(
        icon = UiIcon.FromVector(Icons.Default.Clear),
        modifier = modifier,
        size = size,
        contentDescription = contentDescription,
        tint = tint
    )
}

private val DefaultIconTint: Color
    @Composable
    get() = LocalContentColor.current.copy(
        alpha = LocalContentAlpha.current
    )

private val DefaultIconSize: Dp
    get() = 24.dp