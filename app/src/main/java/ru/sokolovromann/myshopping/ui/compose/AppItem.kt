package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.ui.model.UiString

@Composable
fun AppMultiColumnsItem(
    modifier: Modifier = Modifier,
    multiColumns: Boolean,
    title: @Composable (() -> Unit)? = null,
    body: @Composable (() -> Unit)? = null,
    left: @Composable (() -> Unit)? = null,
    top: @Composable (() -> Unit)? = null,
    right: @Composable (() -> Unit)? = null,
    bottom: @Composable (() -> Unit)? = null,
    dropdownMenu: @Composable (() -> Unit)? = null,
    clickableEnabled: Boolean = true,
    longClickableEnabled: Boolean = true,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    backgroundColor: Color = getAppItemBackgroundColor(selected = false),
    contentColor: Color = contentColorFor(backgroundColor)
) {
    if (multiColumns) {
        AppSurfaceItem(
            modifier = modifier,
            title = title,
            body = body,
            left = left,
            top = top,
            right = right,
            bottom = bottom,
            dropdownMenu = dropdownMenu,
            clickableEnabled = clickableEnabled,
            longClickableEnabled = longClickableEnabled,
            onClick = onClick,
            onLongClick = onLongClick,
            backgroundColor = backgroundColor,
            contentColor = contentColor
        )
    } else {
        AppItem(
            modifier = modifier,
            title = title,
            body = body,
            left = left,
            top = top,
            right = right,
            bottom = bottom,
            dropdownMenu = dropdownMenu,
            clickableEnabled = clickableEnabled,
            longClickableEnabled = longClickableEnabled,
            onClick = onClick,
            onLongClick = onLongClick,
            backgroundColor = backgroundColor,
            contentColor = contentColor
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppItem(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    body: @Composable (() -> Unit)? = null,
    left: @Composable (() -> Unit)? = null,
    top: @Composable (() -> Unit)? = null,
    right: @Composable (() -> Unit)? = null,
    bottom: @Composable (() -> Unit)? = null,
    dropdownMenu: @Composable (() -> Unit)? = null,
    clickableEnabled: Boolean = true,
    longClickableEnabled: Boolean = true,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    backgroundColor: Color = getAppItemBackgroundColor(selected = false),
    contentColor: Color = contentColorFor(backgroundColor)
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AppItemMinHeight)
            .background(color = backgroundColor)
            .combinedClickable(
                onClick = if (clickableEnabled) {
                    onClick
                } else {
                    {}
                },
                onLongClick = if (longClickableEnabled) {
                    onLongClick
                } else {
                    null
                }
            )
            .then(modifier),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        AppItemImpl(
            modifier = Modifier.padding(AppItemPaddings),
            title = title,
            body = body,
            left = left,
            top = top,
            right = right,
            bottom = bottom,
            dropdownMenu = dropdownMenu,
            contentColor = contentColor
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppSurfaceItem(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    body: @Composable (() -> Unit)? = null,
    left: @Composable (() -> Unit)? = null,
    top: @Composable (() -> Unit)? = null,
    right: @Composable (() -> Unit)? = null,
    bottom: @Composable (() -> Unit)? = null,
    dropdownMenu: @Composable (() -> Unit)? = null,
    clickableEnabled: Boolean = true,
    longClickableEnabled: Boolean = true,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    backgroundColor: Color = getAppItemBackgroundColor(selected = false),
    contentColor: Color = contentColorFor(backgroundColor)
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AppSurfaceItemMinHeight)
            .padding(AppSurfaceItemSurfacePaddings)
            .combinedClickable(
                onClick = if (clickableEnabled) {
                    onClick
                } else {
                    {}
                },
                onLongClick = if (longClickableEnabled) {
                    onLongClick
                } else {
                    null
                }
            )
            .then(modifier),
        shape = MaterialTheme.shapes.medium,
        elevation = AppSurfaceItemElevation,
        color = backgroundColor,
        contentColor = contentColor
    ) {
        AppItemImpl(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppItemPaddings),
            title = title,
            body = body,
            left = left,
            top = top,
            right = right,
            bottom = bottom,
            dropdownMenu = dropdownMenu,
            contentColor = contentColor
        )
    }
}

@Composable
fun AppHeaderItem(
    text: UiString,
    modifier: Modifier = Modifier,
    backgroundColor: Color = getAppHeaderItemBackgroundColor(),
    contentColor: Color = contentColorFor(backgroundColor)
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProvideAppHeaderItemTextStyle(contentColor) {
            Text(
                text = text.asCompose(),
                modifier = Modifier.padding(AppItemPaddings)
            )
        }
    }
}

@Composable
fun itemOrNull(enabled: Boolean, content: @Composable () -> Unit): @Composable (() -> Unit)? {
    return if (enabled) content else null
}

@Composable
fun getAppItemBackgroundColor(selected: Boolean, completed: Boolean, noSplit: Boolean): Color {
    return if (selected) {
        MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled)
    } else {
        if (noSplit) {
            MaterialTheme.colors.surface
        } else {
            if (completed) {
                MaterialTheme.colors.background
            } else {
                MaterialTheme.colors.surface
            }
        }
    }
}

@Composable
fun getAppItemBackgroundColor(selected: Boolean): Color {
    return if (selected) {
        MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled)
    } else {
        MaterialTheme.colors.surface
    }
}

@Composable
fun getAppHeaderItemBackgroundColor(): Color {
    return MaterialTheme.colors.background
}

@Composable
private fun AppItemImpl(
    modifier: Modifier,
    title: @Composable (() -> Unit)?,
    body: @Composable (() -> Unit)?,
    left: @Composable (() -> Unit)?,
    top: @Composable (() -> Unit)?,
    right: @Composable (() -> Unit)?,
    bottom: @Composable (() -> Unit)?,
    dropdownMenu: @Composable (() -> Unit)?,
    contentColor: Color
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        top?.let {
            it()
            Spacer(modifier = Modifier.size(AppItemSpacerSmallSize))
        }

        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            left?.let {
                Spacer(modifier = Modifier.size(AppItemSpacerMediumSize))
                it()
                Spacer(modifier = Modifier.size(AppItemSpacerLargeSize))
            }

            Column(modifier = Modifier.weight(1f)) {
                title?.let {
                    ProvideAppItemTitleTextStyle(
                        contentColor = contentColor,
                        content = it
                    )
                }
                body?.let {
                    ProvideAppItemBodyTextStyle(
                        contentColor = contentColor,
                        content = it
                    )
                }
                dropdownMenu?.let { it() }
            }

            right?.let {
                Spacer(modifier = Modifier.size(AppItemSpacerMediumSize))
                it()
            }
        }

        bottom?.let {
            Spacer(modifier = Modifier.size(AppItemSpacerSmallSize))
            it()
        }
    }
}

@Composable
private fun ProvideAppItemTitleTextStyle(contentColor: Color, content: @Composable () -> Unit) {
    ProvideTextStyle(
        value = MaterialTheme.typography.subtitle1.copy(color = contentColor),
        content = content
    )
}

@Composable
private fun ProvideAppItemBodyTextStyle(contentColor: Color, content: @Composable () -> Unit) {
    val color = contentColor.copy(alpha = ContentAlpha.medium)
    ProvideTextStyle(
        value = MaterialTheme.typography.body2.copy(color = color),
        content = content
    )
}

@Composable
private fun ProvideAppHeaderItemTextStyle(contentColor: Color, content: @Composable () -> Unit) {
    val color = contentColor.copy(alpha = ContentAlpha.medium)
    ProvideTextStyle(
        value = MaterialTheme.typography.body1.copy(color = color),
        content = content
    )
}

private val AppItemMinHeight = 48.dp
private val AppItemPaddings = PaddingValues(all = 8.dp)
private val AppItemSpacerSmallSize = 4.dp
private val AppItemSpacerMediumSize = 8.dp
private val AppItemSpacerLargeSize = 16.dp

private val AppSurfaceItemMinHeight = 56.dp
private val AppSurfaceItemSurfacePaddings = PaddingValues(all = 4.dp)
private val AppSurfaceItemElevation = 1.dp