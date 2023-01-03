package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        content = { content(it) }
    )
}