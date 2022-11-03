package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.theme.AppColor

data class NavigationDrawerData(
    val header: TextData = TextData.Header.copy(
        text = UiText.FromResources(R.string.route_header),
        color = ColorData(appColor = AppColor.OnBackground)
    ),
    val items: List<RouteItemData>,
    val backgroundColor: ColorData = ColorData(appColor = AppColor.Background)
) {

    companion object {
        fun defaultItems(checked: UiRoute): List<RouteItemData> = listOf(
            RouteItemData(
                uiText = UiText.FromResources(R.string.route_purchasesName),
                uiIcon = UiIcon.FromResources(R.drawable.ic_all_purchases),
                checked = checked == UiRoute.Purchases
            ),
            RouteItemData(
                uiText = UiText.FromResources(R.string.route_archiveName),
                uiIcon = UiIcon.FromResources(R.drawable.ic_all_archive),
                checked = checked == UiRoute.Archive
            ),
            RouteItemData(
                uiText = UiText.FromResources(R.string.route_trashName),
                uiIcon = UiIcon.FromVector(Icons.Default.Delete),
                checked = checked == UiRoute.Trash
            ),
            RouteItemData(
                uiText = UiText.FromResources(R.string.route_autocompletesName),
                uiIcon = UiIcon.FromVector(Icons.Default.List),
                checked = checked == UiRoute.Autocompletes
            ),
            RouteItemData(
                uiText = UiText.FromResources(R.string.route_settingsName),
                uiIcon = UiIcon.FromVector(Icons.Default.Settings),
                checked = checked == UiRoute.Settings
            )
        )
    }
}