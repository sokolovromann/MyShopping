package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.ui.unit.Dp
import ru.sokolovromann.myshopping.ui.theme.AppColor

data class IconData(
    val icon: UiIcon = UiIcon.Nothing,
    val contentDescription: UiText = UiText.Nothing,
    val size: Dp = Dp.Unspecified,
    val tint: ColorData = ColorData(
        appColor = AppColor.OnSurface,
        alpha = 0.7f
    )
)