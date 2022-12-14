package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.state.UiIcon

@Composable
fun AppDrawerContent(
    modifier: Modifier = Modifier,
    selected: UiRoute,
    onItemClick: (UiRoute) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = appDrawerContentBackgroundColor())
        .then(modifier)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(AppDrawerContentHeaderHeight)
                .padding(AppDrawerContentHeaderPaddings),
        ) {
            ProvideTextStyle(
                value = MaterialTheme.typography.h6.copy(
                    color = contentColorFor(appDrawerContentBackgroundColor())
                ),
                content = { Text(text = stringResource(R.string.drawer_header)) }
            )
        }

        Spacer(modifier = Modifier.size(AppDrawerContentMediumSpacer))

        AppItem(
            onClick = { onItemClick(UiRoute.Purchases) },
            before = {
                AppDrawerContentItemIcon(
                    icon = UiIcon.FromResources(R.drawable.ic_all_purchases),
                    selected = selected == UiRoute.Purchases
                )
            },
            title = { Text(text = stringResource(R.string.drawer_action_openPurchases)) },
            backgroundColor = appDrawerContentBackgroundColor(selected == UiRoute.Purchases)
        )

        AppItem(
            onClick = { onItemClick(UiRoute.Archive) },
            before = {
                AppDrawerContentItemIcon(
                    icon = UiIcon.FromResources(R.drawable.ic_all_archive),
                    selected = selected == UiRoute.Archive
                )
            },
            title = { Text(text = stringResource(R.string.drawer_action_openArchive)) },
            backgroundColor = appDrawerContentBackgroundColor(selected == UiRoute.Archive)
        )

        AppItem(
            onClick = { onItemClick(UiRoute.Trash) },
            before = {
                AppDrawerContentItemIcon(
                    icon = UiIcon.FromVector(Icons.Default.Delete),
                    selected = selected == UiRoute.Trash
                )
            },
            title = { Text(text = stringResource(R.string.drawer_action_openTrash)) },
            backgroundColor = appDrawerContentBackgroundColor(selected == UiRoute.Trash)
        )

        AppItem(
            onClick = { onItemClick(UiRoute.Autocompletes) },
            before = {
                AppDrawerContentItemIcon(
                    icon = UiIcon.FromVector(Icons.Default.List),
                    selected = selected == UiRoute.Autocompletes
                )
            },
            title = { Text(text = stringResource(R.string.drawer_action_openAutocompletes)) },
            backgroundColor = appDrawerContentBackgroundColor(selected == UiRoute.Autocompletes)
        )

        AppItem(
            onClick = { onItemClick(UiRoute.Settings) },
            before = {
                AppDrawerContentItemIcon(
                    icon = UiIcon.FromVector(Icons.Default.Settings),
                    selected = selected == UiRoute.Settings
                )
            },
            title = { Text(text = stringResource(R.string.drawer_action_openSettings)) },
            backgroundColor = appDrawerContentBackgroundColor(selected == UiRoute.Settings)
        )
    }
}

@Composable
private fun appDrawerContentBackgroundColor() = MaterialTheme.colors.background

@Composable
private fun appDrawerContentBackgroundColor(selected: Boolean): Color {
    return if (selected) {
        MaterialTheme.colors.surface
    } else {
        appDrawerContentBackgroundColor()
    }
}

private val AppDrawerContentHeaderHeight = 56.dp
private val AppDrawerContentHeaderPaddings = PaddingValues(
    horizontal = 16.dp
)
private val AppDrawerContentMediumSpacer = 8.dp