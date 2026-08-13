package ru.sokolovromann.myshopping.core.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.sokolovromann.myshopping.core.ui.R
import ru.sokolovromann.myshopping.core.ui.model.NavigationIconType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultAppTopBar(
    header: String? = null,
    navigationIconType: NavigationIconType? = null,
    onNavigationIconClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { header?.let { Text(it) } },
        navigationIcon = {
            navigationIconType?.let {
                IconButton(
                    onClick = { onNavigationIconClick?.invoke() },
                    enabled = onNavigationIconClick != null,
                    content = { NavigationIcon(it) }
                )
            }
        },
        actions = actions
    )
}

@Composable
private fun NavigationIcon(type: NavigationIconType) = when (type) {
    NavigationIconType.Back -> {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            stringResource(R.string.navigation_icon_back)
        )
    }
    NavigationIconType.Close -> {
        Icon(
            Icons.Default.Close,
            stringResource(R.string.navigation_icon_close)
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
            type.icon.asPainter(),
            type.description
        )
    }
}