package ru.sokolovromann.myshopping.core.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun SimpleVerticalGrid(
    cells: Int,
    modifier: Modifier = Modifier,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    itemSpacingEnabled: Boolean = true,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    content: LazyStaggeredGridScope.() -> Unit
) {
    val contentPadding = if (itemSpacingEnabled) 16.dp else 0.dp
    val itemSpacing = if (itemSpacingEnabled) 8.dp else 0.dp
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Fixed(cells),
        state = state,
        contentPadding = PaddingValues(all = contentPadding),
        verticalItemSpacing = itemSpacing,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        flingBehavior = flingBehavior,
        content = content
    )
}

@Composable
fun GridSelectableItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    itemSpacingEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    GridSurfaceItemImpl(
        isSelected = isSelected,
        onClick = onClick,
        onLongClick = onLongClick,
        itemSpacingEnabled = itemSpacingEnabled,
        content = content
    )
}

@Composable
fun GridItem(
    onClick: () -> Unit,
    itemSpacingEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    GridSurfaceItemImpl(
        onClick = onClick,
        itemSpacingEnabled = itemSpacingEnabled,
        content = content
    )
}

@Composable
fun GridBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .padding(horizontal = 16.dp)
            .then(modifier),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
fun BottomGridSpacer() {
    Spacer(Modifier
        .fillMaxWidth()
        .height(156.dp)
    )
}

@Composable
fun GridSurfaceItemImpl(
    onClick: () -> Unit,
    isSelected: Boolean? = null,
    onLongClick: (() -> Unit)? = null,
    itemSpacingEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = if (itemSpacingEnabled) {
        MaterialTheme.shapes.medium
    } else {
        RectangleShape
    }
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected == true) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            Color.Transparent
        },
        label = "CardBackgroundAnimation"
    )
    val tonalElevation = if (itemSpacingEnabled) {
        if (isSelected == true) 0.dp else 2.dp
    } else 0.dp
    val border = if (itemSpacingEnabled) {
        if (isSelected == true) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        }
    } else null
    val contentColor by animateColorAsState(
        targetValue = if (isSelected == true) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        label = "ContentColorAnimation"
    )
    val contentPadding = if (isSelected == true) {
        PaddingValues(end = 24.dp)
    } else {
        PaddingValues(end = 0.dp)
    }
    Surface(
        modifier = Modifier
            .clip(shape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = shape,
        color = backgroundColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        border = border,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .padding(contentPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                CompositionLocalProvider(
                    value = LocalContentColor provides contentColor,
                    content = { content() }
                )
            }

            if (isSelected == true) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(20.dp)
                )
            }
        }
    }
}