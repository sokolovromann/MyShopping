package ru.sokolovromann.myshopping.core.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.sokolovromann.myshopping.core.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationTopAppBar(
    header: String,
    onNavigationIconClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = { Text(header) },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    Icons.Default.Menu,
                    stringResource(R.string.navigation_icon_menu)
                )
            }
        },
        actions = actions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedTopAppBar(
    count: Int,
    onCancelClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit,
) {
    TopAppBar(
        title = { Text(count.toString()) },
        navigationIcon = {
            IconButton(onClick = onCancelClick) {
                Icon(
                    Icons.Default.Cancel,
                    stringResource(R.string.navigation_icon_cancel)
                )
            }
        },
        actions = actions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTopAppBar(
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onCancelClick) {
                Icon(
                    Icons.Default.Cancel,
                    stringResource(R.string.navigation_icon_cancel)
                )
            }
        },
        actions = {
            TextButton(onClick = onSaveClick) {
                Text(stringResource(R.string.top_app_bar_button_save))
            }
        }
    )
}