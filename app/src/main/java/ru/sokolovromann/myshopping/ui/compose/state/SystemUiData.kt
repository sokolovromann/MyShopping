package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.ui.theme.AppColor

data class SystemUiData(
    val statusBarColor: ColorData = Background.statusBarColor,
    val statusBarIconsColor: ColorData = Background.statusBarIconsColor,
    val navigationBarColor: ColorData = Background.navigationBarColor,
    val navigationBarIconsColor: ColorData = Background.navigationBarIconsColor
) {

    companion object {
        val Surface = SystemUiData(
            statusBarColor = ColorData(appColor = AppColor.Primary),
            statusBarIconsColor = ColorData(appColor = AppColor.OnPrimary),
            navigationBarColor = ColorData(appColor = AppColor.Surface),
            navigationBarIconsColor = ColorData(appColor = AppColor.OnSurface)
        )

        val Background = SystemUiData(
            statusBarColor = ColorData(appColor = AppColor.Primary),
            statusBarIconsColor = ColorData(appColor = AppColor.OnPrimary),
            navigationBarColor = ColorData(appColor = AppColor.Background),
            navigationBarIconsColor = ColorData(appColor = AppColor.OnBackground)
        )
    }
}