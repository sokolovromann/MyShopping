package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ru.sokolovromann.myshopping.ui.theme.AppColor

data class ColorData(
    val light: AppColor,
    val lightAlpha: Float? = null,
    val dark: AppColor,
    val darkAlpha: Float? = null,
) {
    constructor(
        appColor: AppColor,
        alpha: Float? = null
    ) : this(light = appColor, lightAlpha = alpha, dark = appColor, darkAlpha = alpha)

    @Composable
    fun color(): Color {
        return if (isSystemInDarkTheme()) {
            if (darkAlpha == null) {
                dark.dark
            } else {
                dark.dark.copy(alpha = darkAlpha)
            }
        } else {
            if (lightAlpha == null) {
                light.light
            } else {
                light.light.copy(alpha = lightAlpha)
            }
        }
    }
}