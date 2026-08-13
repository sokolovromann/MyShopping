package ru.sokolovromann.myshopping.core.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Settings
import ru.sokolovromann.myshopping.core.ui.R

sealed class NavigationItem(
    val text: UiText,
    val icon: UiIcon
) {

    data object Purchases : NavigationItem(
        UiText.FromResources(R.string.navigation_item_purchases),
        AppIcons.Purchases
    )

    data object Archive : NavigationItem(
        UiText.FromResources(R.string.navigation_item_archive),
        UiIcon.FromVector(Icons.Default.Archive)
    )

    data object Trash : NavigationItem(
        UiText.FromResources(R.string.navigation_item_trash),
        AppIcons.Trash
    )

    data object Dictionary : NavigationItem(
        UiText.FromResources(R.string.navigation_item_dictionary),
        AppIcons.Dictionary
    )

    data object Settings : NavigationItem(
        UiText.FromResources(R.string.navigation_item_settings),
        UiIcon.FromVector(Icons.Default.Settings)
    )

    data object About : NavigationItem(
        UiText.FromResources(R.string.navigation_item_about),
        AppIcons.About
    )
}