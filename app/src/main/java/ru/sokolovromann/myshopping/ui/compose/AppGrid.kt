package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.data.model.DeviceSize

@Composable
fun SmartphoneTabletAppGrid(
    modifier: Modifier = Modifier,
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    multiColumns: Boolean,
    multiColumnsSpace: Boolean = multiColumns,
    deviceSize: DeviceSize,
    topBar: @Composable (RowScope.() -> Unit)? = null,
    bottomBar: @Composable (RowScope.() -> Unit)? = null,
    waiting: @Composable (ColumnScope.() -> Unit)? = { CircularAppGridWaiting() },
    isWaiting: Boolean,
    notFound: @Composable (ColumnScope.() -> Unit)? = null,
    isNotFound: Boolean,
    bottomSpacer: @Composable (() -> Unit)? = { AppGridBottomSpacer() },
    items: LazyStaggeredGridScope.() -> Unit
) {
    if (isWaiting) {
        AppGridWaiting(
            modifier = modifier,
            waiting = waiting
        )
    } else {
        if (isNotFound) {
            AppGridNotFound(
                modifier = modifier,
                topBar = topBar,
                notFound = notFound
            )
        } else {
            AppGridShowing(
                modifier = modifier,
                state = gridState,
                multiColumns = multiColumns,
                multiColumnsSpace = multiColumnsSpace,
                deviceSize = deviceSize,
                topBar = topBar,
                bottomBar = bottomBar,
                bottomSpacer = bottomSpacer,
                items = items
            )
        }
    }
}

@Composable
private fun AppGridWaiting(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.background,
    waiting: @Composable (ColumnScope.() -> Unit)?
) {
    waiting?.let {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = backgroundColor)
                .padding(AppGridLoadingContentPaddings)
                .then(modifier),
            content = it
        )
    }
}

@Composable
private fun AppGridNotFound(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor),
    topBar: @Composable (RowScope.() -> Unit)?,
    notFound: @Composable (ColumnScope.() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppGridNothingPaddings)
    ) {
        topBar?.let {
            AppGridBar(content = it)
        }

        notFound?.let {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = backgroundColor)
                    .padding(AppGridNotFoundContentPaddings)
                    .then(modifier)
            ) {
                ProvideTextStyle(
                    value = MaterialTheme.typography.subtitle1.copy(
                        color = contentColor
                    ),
                    content = { it() }
                )
            }
        }
    }
}

@Composable
private fun AppGridShowing(
    modifier: Modifier = Modifier,
    state: LazyStaggeredGridState,
    multiColumns: Boolean,
    multiColumnsSpace: Boolean,
    deviceSize: DeviceSize,
    topBar: @Composable (RowScope.() -> Unit)?,
    bottomBar: @Composable (RowScope.() -> Unit)?,
    bottomSpacer: @Composable (() -> Unit)?,
    items: LazyStaggeredGridScope.() -> Unit
) {
    val columns = if (multiColumns) {
        if (LocalConfiguration.current.screenWidthDp < AppGridMediumMinColumnSize.value * 2) {
            StaggeredGridCells.Fixed(2)
        } else {
            val minSize = when (deviceSize) {
                DeviceSize.Large -> AppGridLargeMinColumnSize
                else -> AppGridMediumMinColumnSize
            }
            StaggeredGridCells.Adaptive(minSize)
        }
    } else {
        StaggeredGridCells.Fixed(1)
    }

    val padding = if (multiColumnsSpace) {
        AppGridMultiColumnsPaddings
    } else {
        AppGridSingleColumnsPaddings
    }

    LazyVerticalStaggeredGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .then(modifier),
        columns = columns,
        state = state
    ) {
        topBar?.let {
            item(
                span = StaggeredGridItemSpan.FullLine,
                content = {
                    AppGridBar(
                        modifier = Modifier.padding(padding),
                        content = it
                    )
                }
            )
        }

        if (multiColumnsSpace) {
            item(
                span = StaggeredGridItemSpan.FullLine,
                content = { Spacer(modifier = Modifier.size(AppGridMultiColumnsSpacerSize)) }
            )
        }

        items()

        bottomBar?.let {
            item(
                span = StaggeredGridItemSpan.FullLine,
                content = {
                    AppGridBar(
                        modifier = Modifier.padding(padding),
                        content = it
                    )
                }
            )
        }

        bottomSpacer?.let {
            item(
                span = StaggeredGridItemSpan.FullLine,
                content = { it() }
            )
        }
    }
}

@Composable
private fun AppGridBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        content = content
    )
}

@Composable
private fun AppGridBottomSpacer() {
    Spacer(modifier = Modifier.height(AppGridSpacerHeight))
}

@Composable
private fun CircularAppGridWaiting() {
    AppGridWaiting{ CircularProgressIndicator() }
}

private val AppGridMediumMinColumnSize = 200.dp
private val AppGridLargeMinColumnSize = 280.dp
private val AppGridSpacerHeight = 128.dp
private val AppGridLoadingContentPaddings = PaddingValues(
    vertical = 8.dp,
    horizontal = 16.dp
)
private val AppGridNotFoundContentPaddings = PaddingValues(
    vertical = 8.dp,
    horizontal = 16.dp
)
private val AppGridMultiColumnsPaddings = PaddingValues(horizontal = 4.dp)
private val AppGridSingleColumnsPaddings = PaddingValues(all = 0.dp)
private val AppGridMultiColumnsSpacerSize = 4.dp
private val AppGridNothingPaddings = PaddingValues(horizontal = 4.dp)