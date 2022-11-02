package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.ui.theme.AppColor

data class BottomBarData(
    val backgroundColor: ColorData = ColorData(
        appColor = AppColor.Background
    ),
    val contentColor: ColorData = ColorData(
        appColor = AppColor.OnBackground
    )
)