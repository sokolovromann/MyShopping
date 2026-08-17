package ru.sokolovromann.myshopping.core.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.core.ui.R
import ru.sokolovromann.myshopping.core.ui.model.NavigationIconType
import ru.sokolovromann.myshopping.core.ui.model.NavigationItem
import ru.sokolovromann.myshopping.core.ui.utils.getWindowSize

@Composable
fun SimpleScaffold(
    header: String,
    navigationIconType: NavigationIconType,
    onNavigationIconClick: () -> Unit,
    onBackClick: () -> Unit,
    topBarActions: @Composable RowScope.() -> Unit = {},
    bottomBar: @Composable (() -> Unit) = {},
    content: @Composable BoxScope.() -> Unit
) {
    BackHandler { onBackClick() }
    Scaffold(
        topBar = {
            TopAppBar(
                header = header,
                navigationIconType = navigationIconType,
                onNavigationIconClick = onNavigationIconClick,
                actions = topBarActions
            )
        },
        bottomBar = bottomBar,
        content = { paddings ->
            Box(
                modifier = Modifier.padding(paddings),
                contentAlignment = Alignment.TopStart,
                content = content
            )
        }
    )
}

@Composable
fun NavigationScaffold(
    initialNavigationItem: NavigationItem,
    onNavigationItemSelected: (NavigationItem) -> Unit,
    onBackClick: () -> Unit,
    header: String = initialNavigationItem.text.asCompose(),
    navigationIconType: NavigationIconType = NavigationIconType.Menu,
    topBarActions: @Composable (RowScope.() -> Unit) = {},
    bottomBar: @Composable (() -> Unit) = {},
    content: @Composable BoxScope.() -> Unit
) {
    val isWidthExpanded = getWindowSize().widthSizeClass == WindowWidthSizeClass.Expanded
    val drawerState = rememberDrawerState(
        initialValue = if (isWidthExpanded) DrawerValue.Open else DrawerValue.Closed
    )
    val scope = rememberCoroutineScope()
    fun openDrawer() = scope.launch { drawerState.open() }
    fun closeDrawer() = scope.launch { drawerState.close() }

    NavigationDrawer(
        drawerState = drawerState,
        initialItem = initialNavigationItem,
        onItemClick = {
            if (!isWidthExpanded) closeDrawer()
            onNavigationItemSelected(it)
        }
    ) {
        SimpleScaffold(
            header = header,
            navigationIconType = navigationIconType,
            onNavigationIconClick = { openDrawer() },
            onBackClick = {
                if (!isWidthExpanded && drawerState.isOpen) {
                    closeDrawer()
                } else {
                    onBackClick()
                }
            },
            topBarActions = topBarActions,
            bottomBar = bottomBar,
            content = content
        )
    }
}

@Composable
private fun NavigationDrawer(
    drawerState: DrawerState,
    initialItem: NavigationItem,
    onItemClick: (NavigationItem) -> Unit,
    content: @Composable () -> Unit
) {
    val windowSize = getWindowSize()
    val widthSize = windowSize.widthSizeClass
    val heightSize = windowSize.heightSizeClass
    val displayModal = widthSize == WindowWidthSizeClass.Compact ||
            widthSize == WindowWidthSizeClass.Medium ||
            heightSize == WindowHeightSizeClass.Compact
    if (displayModal) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(300.dp),
                    content = { NavigationDrawerSheetContent(initialItem, onItemClick) }
                )
            },
            content = content
        )
    } else {
        DismissibleNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DismissibleDrawerSheet {
                    NavigationDrawerSheetContent(initialItem, onItemClick)
                }
            },
            content = content
        )
    }
}

@Composable
private fun NavigationDrawerSheetContent(
    initialItem: NavigationItem,
    onItemClick: (NavigationItem) -> Unit
) {
    Text(
        text = stringResource(R.string.navigation_header),
        modifier = Modifier.padding(all = 16.dp),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        style = MaterialTheme.typography.titleLarge
    )

    val scrollState = rememberScrollState()
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        listOf(
            NavigationItem.Purchases,
            NavigationItem.Archive,
            NavigationItem.Trash,
            NavigationItem.Dictionary,
            NavigationItem.Settings,
            NavigationItem.About
        ).forEach { item ->
            NavigationDrawerItem(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 0.dp),
                icon = { Icon(item.icon.asPainter(), item.text.asCompose()) },
                label = { Text(item.text.asCompose()) },
                selected = item == initialItem,
                onClick = { onItemClick(item) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    header: String,
    navigationIconType: NavigationIconType,
    onNavigationIconClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit
) {
    TopAppBar(
        title = { Text(header) },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                when (navigationIconType) {
                    NavigationIconType.Back -> {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            stringResource(R.string.navigation_icon_back)
                        )
                    }
                    NavigationIconType.Cancel -> {
                        Icon(
                            Icons.Default.Cancel,
                            stringResource(R.string.navigation_icon_cancel)
                        )
                    }
                    NavigationIconType.Menu -> {
                        Icon(
                            Icons.Default.Menu,
                            stringResource(R.string.navigation_icon_menu)
                        )
                    }
                    is NavigationIconType.Other -> {
                        Icon(
                            navigationIconType.icon.asPainter(),
                            navigationIconType.description
                        )
                    }
                }
            }
        },
        actions = actions
    )
}