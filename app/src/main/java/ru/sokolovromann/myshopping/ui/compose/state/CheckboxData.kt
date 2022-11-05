package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.ui.theme.AppColor

data class CheckboxData(
    val checked: Boolean = false,
    val checkedColor: ColorData = ColorData(
        appColor = AppColor.OnSurface,
        alpha = 0.7f
    ),
    val uncheckedColor: ColorData = ColorData(
        appColor = AppColor.OnSurface
    ),
    val checkmarkColor: ColorData = ColorData(
        appColor = AppColor.Surface
    )
)