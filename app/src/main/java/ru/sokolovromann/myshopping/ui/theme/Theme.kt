package ru.sokolovromann.myshopping.ui.theme

import androidx.compose.material.Typography
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColors(
    primary = Green900,
    primaryVariant = Green900,
    secondary = Green900,
    secondaryVariant = Green900,
    surface = Color.White,
    background = Gray200,
    error = Color.Red,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onSurface = Color.Black,
    onBackground = Color.Black,
    onError = Color.Black
)

private val DarkColorPalette = darkColors(
    primary = Green300,
    primaryVariant = Green300,
    secondary = Green300,
    secondaryVariant = Green300,
    surface = Gray900,
    background = Color.Black,
    error = Color.Red,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onSurface = Color.White,
    onBackground = Color.White,
    onError = Color.Black
)

@Composable
fun MyShoppingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    typography: Typography = createTypography(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = Shapes,
        content = content
    )
}