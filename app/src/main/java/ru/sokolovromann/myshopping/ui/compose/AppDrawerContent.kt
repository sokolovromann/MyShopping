package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.model.UiIcon

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
            left = {
                DefaultIcon(
                    icon = UiIcon.Purchases,
                    tint = createIconTint(selected == UiRoute.Purchases)
                )
            },
            title = { AppDrawerItemTitle(text = stringResource(R.string.drawer_action_openPurchases)) },
            backgroundColor = appDrawerContentBackgroundColor(selected == UiRoute.Purchases)
        )

        AppItem(
            onClick = { onItemClick(UiRoute.Archive) },
            left = {
                DefaultIcon(
                    icon = UiIcon.Archive,
                    tint = createIconTint(selected == UiRoute.Archive)
                )
            },
            title = { AppDrawerItemTitle(text = stringResource(R.string.drawer_action_openArchive)) },
            backgroundColor = appDrawerContentBackgroundColor(selected == UiRoute.Archive)
        )

        AppItem(
            onClick = { onItemClick(UiRoute.Trash) },
            left = {
                DefaultIcon(
                    icon = UiIcon.Delete,
                    tint = createIconTint(selected == UiRoute.Trash)
                )
            },
            title = { AppDrawerItemTitle(text = stringResource(R.string.drawer_action_openTrash)) },
            backgroundColor = appDrawerContentBackgroundColor(selected == UiRoute.Trash)
        )

        AppItem(
            onClick = { onItemClick(UiRoute.Autocompletes) },
            left = {
                DefaultIcon(
                    icon = UiIcon.Autocompletes,
                    tint = createIconTint(selected == UiRoute.Autocompletes)
                )
            },
            title = { AppDrawerItemTitle(text = stringResource(R.string.drawer_action_openAutocompletes)) },
            backgroundColor = appDrawerContentBackgroundColor(selected == UiRoute.Autocompletes)
        )

        AppItem(
            onClick = { onItemClick(UiRoute.Settings) },
            left = {
                DefaultIcon(
                    icon = UiIcon.Settings,
                    tint = createIconTint(selected == UiRoute.Settings)
                )
            },
            title = { AppDrawerItemTitle(text = stringResource(R.string.drawer_action_openSettings)) },
            backgroundColor = appDrawerContentBackgroundColor(selected == UiRoute.Settings)
        )

        AppItem(
            onClick = { onItemClick(UiRoute.About) },
            left = {
                DefaultIcon(
                    icon = UiIcon.About,
                    tint = createIconTint(selected == UiRoute.About)
                )
            },
            title = { AppDrawerItemTitle(text = stringResource(R.string.drawer_action_openAbout)) },
            backgroundColor = appDrawerContentBackgroundColor(selected == UiRoute.About)
        )
    }
}

@Composable
private fun createIconTint(selected: Boolean): Color {
    return if (selected) {
        MaterialTheme.colors.secondary
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
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

@Composable
private fun AppDrawerItemTitle(text: String) {
    Text(
        text = text,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
    )
}

private val AppDrawerContentHeaderHeight = 56.dp
private val AppDrawerContentHeaderPaddings = PaddingValues(
    horizontal = 16.dp
)
private val AppDrawerContentMediumSpacer = 8.dp