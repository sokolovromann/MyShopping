package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.ui.theme.AppColor

data class FloatingActionButtonData(
    val backgroundColor: ColorData = ColorData(appColor = AppColor.Secondary),
    val contentColor: ColorData = ColorData(appColor = AppColor.OnSecondary),
    val icon: IconData = IconData.OnFloatingActionButton,
)