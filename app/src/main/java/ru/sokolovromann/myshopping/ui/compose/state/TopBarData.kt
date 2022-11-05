package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.ui.theme.AppColor

data class TopBarData(
    val title: TextData = TextData(),
    val navigationIcon: IconData = IconData(),
    val backgroundColor: ColorData = ColorData(
        light = AppColor.Primary,
        dark = AppColor.Surface
    ),
    val contentColor: ColorData = ColorData(
        light = AppColor.OnSurface,
        dark = AppColor.OnSurface
    )
)