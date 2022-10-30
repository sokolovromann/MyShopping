package ru.sokolovromann.myshopping.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

sealed class AppTypography(val textStyle: TextStyle) {
    object H5 : AppTypography(
        TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            letterSpacing = 0.sp
        )
    )

    object H6 : AppTypography(
        TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            letterSpacing = 0.15.sp
        )
    )

    object Subtitle1 : AppTypography(
        TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            letterSpacing = 0.15.sp
        )
    )

    object Subtitle2 : AppTypography(
        TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            letterSpacing = 0.1.sp
        )
    )

    object Body1 : AppTypography(
        TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    )

    object Body2 : AppTypography(
        TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            letterSpacing = 0.25.sp
        )
    )

    object Caption : AppTypography(
        TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            letterSpacing = 0.4.sp
        )
    )

    object Overline : AppTypography(
        TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            letterSpacing = 1.5.sp
        )
    )
}

val Typography = Typography(
    h5 = AppTypography.H5.textStyle,
    h6 = AppTypography.H6.textStyle,
    subtitle1 = AppTypography.Subtitle1.textStyle,
    subtitle2 = AppTypography.Subtitle2.textStyle,
    body1 = AppTypography.Body1.textStyle,
    body2 = AppTypography.Body2.textStyle,
    caption = AppTypography.Caption.textStyle,
    overline = AppTypography.Overline.textStyle
)