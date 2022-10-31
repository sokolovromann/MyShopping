package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.ui.theme.AppColor

data class CheckboxData(
    val checked: Boolean = false,
    val checkedColor: ColorData = OnSurface.checkedColor,
    val uncheckedColor: ColorData = OnSurface.uncheckedColor,
    val checkmarkColor: ColorData = OnSurface.checkmarkColor
) {
    companion object {
        val OnSurface = CheckboxData(
            checkedColor = ColorData(appColor = AppColor.OnSurface, alpha = 0.7f),
            uncheckedColor = ColorData(appColor = AppColor.OnSurface),
            checkmarkColor = ColorData(appColor = AppColor.Surface)
        )

        val OnBackground = CheckboxData(
            checkedColor = ColorData(appColor = AppColor.OnBackground, alpha = 0.7f),
            uncheckedColor = ColorData(appColor = AppColor.OnBackground),
            checkmarkColor = ColorData(appColor = AppColor.Background)
        )
    }
}