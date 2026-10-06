package ru.sokolovromann.myshopping.core.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.core.ui.R

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
fun GridItem(
    title: @Composable () -> Unit,
    body: @Composable (() -> Unit)? = null,
    leftIcon: @Composable (() -> Unit)? = null,
    rightIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    borderEnabled: Boolean = true
) {
    if (borderEnabled) {
        GridItemContent(
            title = title,
            body = body,
            leftIcon = leftIcon,
            rightIcon = rightIcon,
            onClick = onClick,
            onLongClick = onLongClick,
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 2.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        )
    } else {
        GridItemContent(
            title = title,
            body = body,
            leftIcon = leftIcon,
            rightIcon = rightIcon,
            onClick = onClick,
            onLongClick = onLongClick
        )
    }
}

@Composable
fun GridSelectableItem(
    isSelected: Boolean,
    title: @Composable () -> Unit,
    body: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    borderEnabled: Boolean = true
) {
    val rightIcon = if (isSelected) {
        @Composable { GridSelectedRightIcon() }
    } else null
    val color by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            Color.Transparent
        },
        label = "CardBackgroundAnimation"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        label = "ContentColorAnimation"
    )
    if (borderEnabled) {
        val tonalElevation = if (isSelected) 0.dp else 2.dp
        val border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        }
        GridItemContent(
            title = title,
            body = body,
            leftIcon = icon,
            rightIcon = rightIcon,
            onClick = onClick,
            onLongClick = onLongClick,
            shape = MaterialTheme.shapes.medium,
            color = color,
            contentColor = contentColor,
            tonalElevation = tonalElevation,
            border = border
        )
    } else {
        GridItemContent(
            title = title,
            body = body,
            leftIcon = icon,
            rightIcon = rightIcon,
            onClick = onClick,
            onLongClick = onLongClick,
            color = color,
            contentColor = contentColor
        )
    }
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
private fun GridItemContent(
    title: @Composable () -> Unit,
    body: @Composable (() -> Unit)?,
    leftIcon: @Composable (() -> Unit)?,
    rightIcon: @Composable (() -> Unit)?,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    shape: Shape = RectangleShape,
    color: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    tonalElevation: Dp = 0.dp,
    border: BorderStroke? = null
) {
    Surface(
        modifier = Modifier
            .clip(shape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = shape,
        color = color,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        border = border,
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            leftIcon?.let {
                it()
                Spacer(Modifier.size(8.dp))
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                CompositionLocalProvider(
                    values = arrayOf(
                        LocalContentColor provides contentColor,
                        LocalTextStyle provides MaterialTheme.typography.bodyLarge
                    ),
                    content = title
                )
                body?.let {
                    CompositionLocalProvider(
                        values = arrayOf(
                            LocalContentColor provides contentColor.copy(alpha = 0.7f),
                            LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                        ),
                        content = {
                            Spacer(Modifier.size(4.dp))
                            it()
                        }
                    )
                }
            }
            rightIcon?.let {
                Spacer(Modifier.size(8.dp))
                it()
            }
        }
    }
}

@Composable
private fun GridSelectedRightIcon() {
    Icon(
        imageVector = Icons.Filled.Check,
        contentDescription = stringResource(R.string.text_selected),
        tint = MaterialTheme.colorScheme.primary
    )
}