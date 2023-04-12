package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

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
        content = {
            val paddings = getAppScaffoldPaddings(it)
            content(paddings)
        }
    )
}

@Composable
private fun getAppScaffoldPaddings(scaffoldPaddings: PaddingValues): PaddingValues {
    val configuration = LocalConfiguration.current
    return if (configuration.screenWidthDp > 700 && configuration.screenHeightDp > 700) {
        PaddingValues(
            start = 72.dp,
            top = scaffoldPaddings.calculateTopPadding(),
            end = 72.dp,
            bottom = scaffoldPaddings.calculateBottomPadding()
        )
    } else {
        scaffoldPaddings
    }
}