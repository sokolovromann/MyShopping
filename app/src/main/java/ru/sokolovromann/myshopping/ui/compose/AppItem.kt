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

@Composable
fun AppMultiColumnsItem(
    modifier: Modifier = Modifier,
    multiColumns: Boolean,
    title: @Composable (() -> Unit)? = null,
    body: @Composable (() -> Unit)? = null,
    before: @Composable (() -> Unit)? = null,
    after: @Composable (() -> Unit)? = null,
    dropdownMenu: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor)
) {
    if (multiColumns) {
        AppSurfaceItem(
            modifier = modifier,
            title = title,
            body = body,
            before = before,
            after = after,
            dropdownMenu = dropdownMenu,
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
            before = before,
            after = after,
            dropdownMenu = dropdownMenu,
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
    before: @Composable (() -> Unit)? = null,
    after: @Composable (() -> Unit)? = null,
    dropdownMenu: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor)
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AppItemMinHeight)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .background(color = backgroundColor)
            .then(modifier),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        AppItemImpl(
            modifier = Modifier.padding(AppItemPaddings),
            before = before,
            title = title,
            body = body,
            after = after,
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
    before: @Composable (() -> Unit)? = null,
    after: @Composable (() -> Unit)? = null,
    dropdownMenu: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor)
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AppSurfaceItemMinHeight)
            .padding(AppSurfaceItemSurfacePaddings)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
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
            before = before,
            title = title,
            body = body,
            after = after,
            dropdownMenu = dropdownMenu,
            contentColor = contentColor
        )
    }
}

@Composable
fun itemOrNull(enabled: Boolean, content: @Composable () -> Unit): @Composable (() -> Unit)? {
    return if (enabled) content else null
}

@Composable
private fun AppItemImpl(
    modifier: Modifier,
    title: @Composable (() -> Unit)?,
    body: @Composable (() -> Unit)?,
    before: @Composable (() -> Unit)?,
    after: @Composable (() -> Unit)?,
    dropdownMenu: @Composable (() -> Unit)?,
    contentColor: Color
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        before?.let {
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

        after?.let {
            Spacer(modifier = Modifier.size(AppItemSpacerMediumSize))
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
        value = MaterialTheme.typography.body1.copy(color = color),
        content = content
    )
}

private val AppItemMinHeight = 48.dp
private val AppItemPaddings = PaddingValues(all = 8.dp)
private val AppItemSpacerMediumSize = 8.dp
private val AppItemSpacerLargeSize = 16.dp

private val AppSurfaceItemMinHeight = 56.dp
private val AppSurfaceItemSurfacePaddings = PaddingValues(all = 4.dp)
private val AppSurfaceItemElevation = 1.dp