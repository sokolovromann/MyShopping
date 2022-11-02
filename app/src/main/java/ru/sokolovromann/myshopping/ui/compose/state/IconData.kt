package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.ui.unit.Dp
import ru.sokolovromann.myshopping.ui.theme.AppColor

data class IconData(
    val icon: UiIcon = UiIcon.Nothing,
    val contentDescription: UiText = UiText.Nothing,
    val size: Dp = Dp.Unspecified,
    val tint: ColorData = OnSurface.tint
) {

    companion object {
        val OnSurface: IconData = IconData(
            tint = ColorData(appColor = AppColor.OnSurface, alpha = 0.7f)
        )

        val OnBackground: IconData = IconData(
            tint = ColorData(appColor = AppColor.OnBackground, alpha = 0.7f)
        )

        val OnTopAppBar: IconData = IconData(
            tint = ColorData(
                light = AppColor.OnPrimary,
                lightAlpha = 0.7f,
                dark = AppColor.OnSurface,
                darkAlpha = 0.7f
            )
        )

        val OnBottomAppBar: IconData = IconData(
            tint = ColorData(
                appColor = AppColor.OnBackground,
                alpha = 0.7f
            )
        )

        val OnFloatingActionButton: IconData = IconData(
            tint = ColorData(appColor = AppColor.OnSecondary)
        )
    }
}