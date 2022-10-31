package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.ui.theme.AppColor

data class RadioButtonData(
    val selected: Boolean = false,
    val selectedColor: ColorData,
    val unselectedColor: ColorData
) {

    companion object {
        val OnSurface = RadioButtonData(
            selectedColor = ColorData(appColor = AppColor.OnSurface),
            unselectedColor = ColorData(appColor = AppColor.OnSurface)
        )

        val OnBackground = RadioButtonData(
            selectedColor = ColorData(appColor = AppColor.OnBackground),
            unselectedColor = ColorData(appColor = AppColor.OnBackground)
        )
    }
}