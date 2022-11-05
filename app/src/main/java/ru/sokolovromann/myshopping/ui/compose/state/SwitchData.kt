package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.ui.theme.AppColor

data class SwitchData(
    val checked: Boolean = false,
    val checkedThumbColor: ColorData = ColorData(
        appColor = AppColor.Secondary
    ),
    val checkedTrackColor: ColorData = ColorData(
        appColor = AppColor.OnSurface
    ),
    val uncheckedThumbColor: ColorData = ColorData(
        appColor = AppColor.Surface
    ),
    val uncheckedTrackColor: ColorData = ColorData(
        appColor = AppColor.OnSurface
    )
)