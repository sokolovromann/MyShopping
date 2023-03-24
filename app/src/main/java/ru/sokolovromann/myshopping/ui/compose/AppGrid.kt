package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ceil

@Composable
fun SmartphoneTabletAppGrid(
    modifier: Modifier = Modifier,
    multiColumns: Boolean = false,
    smartphoneScreen: Boolean = true,
    content: @Composable () -> Unit
) {
    val maxColumnWidth = if (smartphoneScreen) {
        AppGridMediumMaxColumnWidth
    } else {
        AppGridLargeMaxColumnWidth
    }

    AppGrid(
        modifier = modifier,
        multiColumns = multiColumns,
        maxColumnWidth = maxColumnWidth,
        content = content
    )
}

@Composable
fun AppGrid(
    modifier: Modifier = Modifier,
    multiColumns: Boolean = false,
    maxColumnWidth: Dp = AppGridMediumMaxColumnWidth,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        check(constraints.hasBoundedWidth) {
            "Unbounded width not supported"
        }

        val columns = if (multiColumns) {
            ceil(constraints.maxWidth / maxColumnWidth.toPx()).toInt()
        } else 1
        val columnWidth = constraints.maxWidth / columns

        val itemConstraints = constraints.copy(maxWidth = columnWidth)
        val colHeights = IntArray(columns) { 0 }
        val placeables = measurables.map { measurable ->
            val column = shortestColumn(colHeights)
            val placeable = measurable.measure(itemConstraints)
            colHeights[column] += placeable.height
            placeable
        }

        val height = colHeights.maxOrNull()?.coerceIn(constraints.minHeight, constraints.maxHeight)
            ?: constraints.minHeight

        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            val colY = IntArray(columns) { 0 }
            placeables.forEach { placeable ->
                val column = shortestColumn(colY)
                placeable.place(
                    x = columnWidth * column,
                    y = colY[column]
                )
                colY[column] += placeable.height
            }
        }
    }
}

private fun shortestColumn(colHeights: IntArray): Int {
    var minHeight = Int.MAX_VALUE
    var column = 0
    colHeights.forEachIndexed { index, height ->
        if (height < minHeight) {
            minHeight = height
            column = index
        }
    }
    return column
}

private val AppGridMediumMaxColumnWidth = 300.dp
private val AppGridLargeMaxColumnWidth = 400.dp