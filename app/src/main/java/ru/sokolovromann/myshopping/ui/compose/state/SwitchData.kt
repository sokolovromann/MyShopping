package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.ui.theme.AppColor

data class SwitchData(
    val checked: Boolean = false,
    val checkedThumbColor: ColorData = OnSurface.checkedThumbColor,
    val checkedTrackColor: ColorData = OnSurface.checkedTrackColor,
    val uncheckedThumbColor: ColorData = OnSurface.uncheckedThumbColor,
    val uncheckedTrackColor: ColorData = OnSurface.uncheckedTrackColor
) {

    companion object {
        val OnSurface = SwitchData(
            checkedThumbColor = ColorData(appColor = AppColor.Secondary),
            checkedTrackColor = ColorData(appColor = AppColor.OnSurface),
            uncheckedThumbColor = ColorData(appColor = AppColor.Surface),
            uncheckedTrackColor = ColorData(appColor = AppColor.OnSurface)
        )

        val OnBackground = SwitchData(
            checkedThumbColor = ColorData(appColor = AppColor.Secondary),
            checkedTrackColor = ColorData(appColor = AppColor.OnBackground),
            uncheckedThumbColor = ColorData(appColor = AppColor.Background),
            uncheckedTrackColor = ColorData(appColor = AppColor.OnBackground)
        )
    }
}