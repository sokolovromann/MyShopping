package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.ui.theme.AppColor

data class NavigationDrawerData(
    val header: TextData = TextData(),
    val items: List<RouteItemData> = listOf(),
    val backgroundColor: ColorData = ColorData(
        appColor = AppColor.Background
    )
)