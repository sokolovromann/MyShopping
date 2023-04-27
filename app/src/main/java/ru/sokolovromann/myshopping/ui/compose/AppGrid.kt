package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.ui.compose.state.ScreenState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SmartphoneTabletAppGrid(
    modifier: Modifier = Modifier,
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    screenState: ScreenState,
    multiColumns: Boolean,
    multiColumnsSpace: Boolean = multiColumns,
    smartphoneScreen: Boolean,
    topBar: @Composable (RowScope.() -> Unit)? = null,
    bottomBar: @Composable (RowScope.() -> Unit)? = null,
    loading: @Composable (ColumnScope.() -> Unit)? = { CircularAppGridLoading() },
    notFound: @Composable (ColumnScope.() -> Unit)? = null,
    saving: @Composable (ColumnScope.() -> Unit)? = { CircularAppGridLoading() },
    bottomSpacer: @Composable (() -> Unit)? = { AppGridBottomSpacer() },
    items: LazyStaggeredGridScope.() -> Unit
) {
    when (screenState) {
        ScreenState.Nothing -> AppGridNotFound(
            modifier = modifier,
            topBar = topBar,
            notFound = notFound
        )

        ScreenState.Loading -> AppGridLoading(
            modifier = modifier,
            loading = loading
        )

        ScreenState.Showing -> AppGridShowing(
            modifier = modifier,
            state = gridState,
            multiColumns = multiColumns,
            multiColumnsSpace = multiColumnsSpace,
            smartphoneScreen = smartphoneScreen,
            topBar = topBar,
            bottomBar = bottomBar,
            bottomSpacer = bottomSpacer,
            items = items
        )

        ScreenState.Saving -> AppGridSaving(
            modifier = modifier,
            topBar = topBar,
            saving = saving
        )
    }
}

@Composable
private fun AppGridLoading(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.background,
    loading: @Composable (ColumnScope.() -> Unit)?
) {
    loading?.let {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppGridShowing(
    modifier: Modifier = Modifier,
    state: LazyStaggeredGridState,
    multiColumns: Boolean,
    multiColumnsSpace: Boolean,
    smartphoneScreen: Boolean,
    topBar: @Composable (RowScope.() -> Unit)?,
    bottomBar: @Composable (RowScope.() -> Unit)?,
    bottomSpacer: @Composable (() -> Unit)?,
    items: LazyStaggeredGridScope.() -> Unit
) {
    val columns = if (multiColumns) {
        val minSize = if (smartphoneScreen) AppGridMediumMinColumnSize else AppGridLargeMinColumnSize
        StaggeredGridCells.Adaptive(minSize)
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
private fun AppGridSaving(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.background,
    topBar: @Composable (RowScope.() -> Unit)?,
    saving: @Composable (ColumnScope.() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppGridNothingPaddings)
    ) {
        topBar?.let {
            AppGridBar(content = it)
        }

        saving?.let {
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
private fun CircularAppGridLoading() {
    AppGridLoading { CircularProgressIndicator() }
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