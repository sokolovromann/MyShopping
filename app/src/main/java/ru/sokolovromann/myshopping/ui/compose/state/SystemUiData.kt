package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.ui.theme.AppColor

data class SystemUiData(
    val statusBarColor: ColorData = ColorData(
        appColor = AppColor.Primary
    ),
    val statusBarIconsColor: ColorData = ColorData(
        appColor = AppColor.OnPrimary
    ),
    val navigationBarColor: ColorData = ColorData(
        appColor = AppColor.Background
    ),
    val navigationBarIconsColor: ColorData = ColorData(
        appColor = AppColor.OnBackground
    )
)