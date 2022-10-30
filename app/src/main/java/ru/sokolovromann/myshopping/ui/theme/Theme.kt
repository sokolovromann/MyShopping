package ru.sokolovromann.myshopping.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val LightColorPalette = lightColors(
    primary = AppColor.Primary.light,
    primaryVariant = AppColor.PrimaryVariant.light,
    secondary = AppColor.Secondary.light,
    secondaryVariant = AppColor.SecondaryVariant.light,
    surface = AppColor.Surface.light,
    background = AppColor.Background.light,
    error = AppColor.Error.light,
    onPrimary = AppColor.OnPrimary.light,
    onSecondary = AppColor.OnSecondary.light,
    onSurface = AppColor.OnSurface.light,
    onBackground = AppColor.OnBackground.light,
    onError = AppColor.OnError.light
)

private val DarkColorPalette = darkColors(
    primary = AppColor.Primary.dark,
    primaryVariant = AppColor.PrimaryVariant.dark,
    secondary = AppColor.Secondary.dark,
    secondaryVariant = AppColor.SecondaryVariant.dark,
    surface = AppColor.Surface.dark,
    background = AppColor.Background.dark,
    error = AppColor.Error.dark,
    onPrimary = AppColor.OnPrimary.dark,
    onSecondary = AppColor.OnSecondary.dark,
    onSurface = AppColor.OnSurface.dark,
    onBackground = AppColor.OnBackground.dark,
    onError = AppColor.OnError.dark
)

@Composable
fun MyShoppingTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}