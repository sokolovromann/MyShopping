package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.sokolovromann.myshopping.ui.compose.state.ScreenState

@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
    systemUiController: SystemUiController = rememberSystemUiController(),
    statusBarColor: Color = MaterialTheme.colors.primarySurface,
    backgroundColor: Color = MaterialTheme.colors.background,
    navigationBarColor: Color = backgroundColor,
    contentColor: Color = contentColorFor(backgroundColor),
    content: @Composable (PaddingValues) -> Unit
) {
    AppSystemUi(
        systemUiController = systemUiController,
        statusBarColor = statusBarColor,
        navigationBarColor = navigationBarColor
    )

    Scaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        drawerContent = drawerContent,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        content = { content(it) }
    )
}

@Composable
fun AppGridScaffold(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    screenState: ScreenState,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
    systemUiController: SystemUiController = rememberSystemUiController(),
    statusBarColor: Color = MaterialTheme.colors.primarySurface,
    backgroundColor: Color = MaterialTheme.colors.background,
    navigationBarColor: Color = backgroundColor,
    contentColor: Color = contentColorFor(backgroundColor),
    loadingContent: @Composable (BoxScope.() -> Unit)? = null,
    notFoundContent: @Composable (ColumnScope.() -> Unit)? = null,
    gridBar: @Composable (RowScope.() -> Unit)? = null,
    gridBottomBar: @Composable (RowScope.() -> Unit)? = null,
    spaceAfterGrid: Boolean = true,
    gridMultiColumnsSpace: Boolean = true,
    gridContent: @Composable ColumnScope.() -> Unit
) {
    AppScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        drawerContent = drawerContent,
        systemUiController = systemUiController,
        statusBarColor = statusBarColor,
        navigationBarColor = navigationBarColor,
        backgroundColor = backgroundColor,
        contentColor = contentColor
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (screenState) {
                ScreenState.Nothing -> AppNothingGridContent(gridBar, notFoundContent)

                ScreenState.Loading -> loadingContent?.let { it() }

                ScreenState.Showing -> AppShowingGridContent(
                    gridBar,
                    gridBottomBar,
                    gridContent,
                    spaceAfterGrid,
                    gridMultiColumnsSpace
                )

                ScreenState.Saving -> {}
            }
        }
    }
}

@Composable
private fun AppNothingGridContent(
    gridBar: @Composable (RowScope.() -> Unit)?,
    notFoundContent: @Composable (ColumnScope.() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppGridScaffoldNothingPaddings)
    ) {
        gridBar?.let {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth(),
                content = it
            )
        }

        notFoundContent?.let { it() }
    }
}

@Composable
private fun AppShowingGridContent(
    gridBar: @Composable (RowScope.() -> Unit)?,
    gridBottomBar: @Composable (RowScope.() -> Unit)?,
    gridContent: @Composable ColumnScope.() -> Unit,
    spaceAfterGrid: Boolean,
    gridMultiColumnsSpace: Boolean
) {
    val scrollState = rememberScrollState()
    val padding = if (gridMultiColumnsSpace) {
        AppGridScaffoldMultiColumnsPaddings
    } else {
        AppGridScaffoldSingleColumnsPaddings
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        if (gridBar != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth(),
                content = gridBar
            )
        }

        if (gridMultiColumnsSpace) {
            Spacer(modifier = Modifier.size(AppGridScaffoldMultiColumnsSpacerSize))
        }

        Column(
            modifier = Modifier.padding(padding),
            content = { gridContent() }
        )

        if (gridBottomBar != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth(),
                content = gridBottomBar
            )
        }

        if (spaceAfterGrid) {
            Spacer(modifier = Modifier.height(AppGridScaffoldSpacerHeight))
        }
    }
}

private val AppGridScaffoldNothingPaddings = PaddingValues(horizontal = 4.dp)
private val AppGridScaffoldMultiColumnsPaddings = PaddingValues(horizontal = 4.dp)
private val AppGridScaffoldSingleColumnsPaddings = PaddingValues(all = 0.dp)
private val AppGridScaffoldMultiColumnsSpacerSize = 4.dp
private val AppGridScaffoldSpacerHeight = 128.dp