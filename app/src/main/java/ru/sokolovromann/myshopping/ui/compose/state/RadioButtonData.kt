package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.ui.theme.AppColor

data class RadioButtonData(
    val selected: Boolean = false,
    val selectedColor: ColorData = ColorData(
        appColor = AppColor.OnSurface
    ),
    val unselectedColor: ColorData = ColorData(
        appColor = AppColor.OnSurface
    )
)