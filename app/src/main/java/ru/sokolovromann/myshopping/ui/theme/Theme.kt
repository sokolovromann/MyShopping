package ru.sokolovromann.myshopping.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

sealed class AppColors(val light: Color, val dark: Color) {
    object Primary : AppColors(Green900, Green300)
    object PrimaryVariant : AppColors(Green900, Green300)
    object Secondary : AppColors(Green900, Green300)
    object SecondaryVariant : AppColors(Green900, Green300)
    object Surface : AppColors(Color.White, Gray900)
    object Background : AppColors(Gray200, Color.Black)
    object Error : AppColors(Color.Red, Color.Red)
    object OnPrimary : AppColors(Color.White, Color.Black)
    object OnSecondary : AppColors(Color.White, Color.Black)
    object OnSurface : AppColors(Color.Black, Color.White)
    object OnBackground : AppColors(Color.Black, Color.White)
    object OnError : AppColors(Color.Black, Color.Black)
}

private val LightColorPalette = lightColors(
    primary = AppColors.Primary.light,
    primaryVariant = AppColors.PrimaryVariant.light,
    secondary = AppColors.Secondary.light,
    secondaryVariant = AppColors.SecondaryVariant.light,
    surface = AppColors.Surface.light,
    background = AppColors.Background.light,
    error = AppColors.Error.light,
    onPrimary = AppColors.OnPrimary.light,
    onSecondary = AppColors.OnSecondary.light,
    onSurface = AppColors.OnSurface.light,
    onBackground = AppColors.OnBackground.light,
    onError = AppColors.OnError.light
)

private val DarkColorPalette = darkColors(
    primary = AppColors.Primary.dark,
    primaryVariant = AppColors.PrimaryVariant.dark,
    secondary = AppColors.Secondary.dark,
    secondaryVariant = AppColors.SecondaryVariant.dark,
    surface = AppColors.Surface.dark,
    background = AppColors.Background.dark,
    error = AppColors.Error.dark,
    onPrimary = AppColors.OnPrimary.dark,
    onSecondary = AppColors.OnSecondary.dark,
    onSurface = AppColors.OnSurface.dark,
    onBackground = AppColors.OnBackground.dark,
    onError = AppColors.OnError.dark
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