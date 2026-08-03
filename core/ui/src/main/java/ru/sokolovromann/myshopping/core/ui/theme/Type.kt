package ru.sokolovromann.myshopping.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun createTypography(fontSize: MyShoppingThemeFontSize): Typography {
    val factor = fontSize.scaleFactor
    return Typography(
        displayLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (57 * factor).sp,
            lineHeight = (64 * factor).sp,
            letterSpacing = 0.sp
        ),
        displayMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (45 * factor).sp,
            lineHeight = (52 * factor).sp,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (36 * factor).sp,
            lineHeight = (44 * factor).sp,
            letterSpacing = 0.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (32 * factor).sp,
            lineHeight = (40 * factor).sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (28 * factor).sp,
            lineHeight = (36 * factor).sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (24 * factor).sp,
            lineHeight = (32 * factor).sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = (22 * factor).sp,
            lineHeight = (28 * factor).sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (16 * factor).sp,
            lineHeight = (24 * factor).sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (14 * factor).sp,
            lineHeight = (20 * factor).sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (16 * factor).sp,
            lineHeight = (24 * factor).sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (14 * factor).sp,
            lineHeight = (20 * factor).sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (12 * factor).sp,
            lineHeight = (16 * factor).sp,
            letterSpacing = 0.4.sp
        ),
        labelLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (14 * factor).sp,
            lineHeight = (20 * factor).sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (12 * factor).sp,
            lineHeight = (16 * factor).sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (11 * factor).sp,
            lineHeight = (16 * factor).sp,
            letterSpacing = 0.5.sp
        )
    )
}


enum class MyShoppingThemeFontSize {

    Small,

    Medium,

    Large,

    ExtraLarge,

    Huge,

    ExtraHuge;

    val scaleFactor: Float
        get() = when (this) {
            Small -> 0.85f
            Medium -> 1.0f
            Large -> 1.15f
            ExtraLarge -> 1.30f
            Huge -> 1.45f
            ExtraHuge -> 1.65f
        }
}