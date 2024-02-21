package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
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
fun NavigationMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = DefaultIconSize,
    contentDescription: UiString? = UiString.FromResources(R.string.all_contentDescription_navigationMenuIcon),
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

@Composable
fun BackScreenIcon(
    modifier: Modifier = Modifier,
    size: Dp = DefaultIconSize,
    contentDescription: UiString? = UiString.FromResources(R.string.all_contentDescription_backScreenIcon),
    tint: Color = DefaultIconTint
) {
    AppTopBarIcon(
        icon = UiIcon.FromVector(Icons.Default.ArrowBack),
        modifier = modifier,
        size = size,
        contentDescription = contentDescription,
        tint = tint
    )
}

@Composable
fun CloseScreenIcon(
    modifier: Modifier = Modifier,
    size: Dp = DefaultIconSize,
    contentDescription: UiString? = UiString.FromResources(R.string.all_contentDescription_closeScreenIcon),
    tint: Color = DefaultIconTint
) {
    AppTopBarIcon(
        icon = UiIcon.FromVector(Icons.Default.Close),
        modifier = modifier,
        size = size,
        contentDescription = contentDescription,
        tint = tint
    )
}

@Composable
fun DeleteDataIcon(
    modifier: Modifier = Modifier,
    size: Dp = DefaultIconSize,
    contentDescription: UiString? = UiString.FromResources(R.string.all_contentDescription_deleteDataIcon),
    tint: Color = DefaultIconTint
) {
    AppTopBarIcon(
        icon = UiIcon.FromVector(Icons.Default.Delete),
        modifier = modifier,
        size = size,
        contentDescription = contentDescription,
        tint = tint
    )
}

@Composable
fun SelectAllDataIcon(
    modifier: Modifier = Modifier,
    size: Dp = DefaultIconSize,
    contentDescription: UiString? = UiString.FromResources(R.string.all_contentDescription_selectAllDataIcon),
    tint: Color = DefaultIconTint
) {
    AppTopBarIcon(
        icon = UiIcon.FromResources(R.drawable.ic_all_select_all),
        modifier = modifier,
        size = size,
        contentDescription = contentDescription,
        tint = tint
    )
}

@Composable
fun AppDropdownMenuIcon(
    icon: UiIcon,
    modifier: Modifier = Modifier,
    size: Dp = DefaultIconSize,
    contentDescription: UiString? = null,
    tint: Color = OnSurfaceTint
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
fun MoreMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = DefaultIconSize,
    contentDescription: UiString? = null,
    tint: Color = OnSurfaceTint
) {
    AppDropdownMenuIcon(
        icon = UiIcon.FromVector(Icons.Default.KeyboardArrowRight),
        modifier = modifier,
        size = size,
        contentDescription = contentDescription,
        tint = tint
    )
}

@Composable
fun AppBottomBarIcon(
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
fun MoreIcon(
    modifier: Modifier = Modifier,
    size: Dp = DefaultIconSize,
    contentDescription: UiString? = null,
    tint: Color = OnSurfaceTint
) {
    AppBottomBarIcon(
        icon = UiIcon.FromVector(Icons.Default.MoreVert),
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

private val OnSurfaceTint: Color
    @Composable
    get() = MaterialTheme.colors.onSurface.copy(
        alpha = ContentAlpha.medium
    )

private val DefaultIconSize: Dp
    get() = 24.dp