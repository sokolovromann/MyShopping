package ru.sokolovromann.myshopping.core.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.core.ui.R
import ru.sokolovromann.myshopping.core.ui.model.NavigationItem

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AppNavigationDrawer(
    drawerState: DrawerState,
    initialItem: NavigationItem,
    onItemClick: (NavigationItem) -> Unit,
    content: @Composable () -> Unit
) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass
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
                    content = { DrawerSheetContent(initialItem, onItemClick) }
                )
            },
            content = content
        )
    } else {
        DismissibleNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DismissibleDrawerSheet {
                    DrawerSheetContent(initialItem, onItemClick)
                }
            },
            content = content
        )
    }
}

@Composable
private fun DrawerSheetContent(
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