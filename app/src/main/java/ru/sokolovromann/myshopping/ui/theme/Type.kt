package ru.sokolovromann.myshopping.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun createTypography(
    fontSizeOffset: FontSizeOffset = FontSizeOffset()
): Typography {
    return Typography(
        h1 = TextStyle(
            fontWeight = FontWeight.Light,
            fontSize = (96 + fontSizeOffset.h1).sp,
            letterSpacing = (-1.5).sp
        ),
        h2 = TextStyle(
            fontWeight = FontWeight.Light,
            fontSize = (60 + fontSizeOffset.h2).sp,
            letterSpacing = (-0.5).sp
        ),
        h3 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = (48 + fontSizeOffset.h3).sp,
            letterSpacing = 0.sp
        ),
        h4 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = (34 + fontSizeOffset.h4).sp,
            letterSpacing = 0.25.sp
        ),
        h5 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = (24 + fontSizeOffset.h5).sp,
            letterSpacing = 0.sp
        ),
        h6 = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = (20 + fontSizeOffset.h6).sp,
            letterSpacing = 0.15.sp
        ),
        subtitle1 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = (16 + fontSizeOffset.subtitle1).sp,
            letterSpacing = 0.15.sp
        ),
        subtitle2 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = (14 + fontSizeOffset.subtitle2).sp,
            letterSpacing = 0.1.sp
        ),
        body1 = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (16 + fontSizeOffset.body1).sp
        ),
        body2 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = (14 + fontSizeOffset.body2).sp,
            letterSpacing = 0.25.sp
        ),
        button = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = (14 + fontSizeOffset.button).sp,
            letterSpacing = 1.25.sp
        ),
        caption = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = (12 + fontSizeOffset.caption).sp,
            letterSpacing = 0.4.sp
        ),
        overline = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = (10 + fontSizeOffset.overline).sp,
            letterSpacing = 1.5.sp
        )
    )
}

data class FontSizeOffset(val offset: Int = 0) {
    val h1: Int = offset
    val h2: Int = offset
    val h3: Int = offset
    val h4: Int = offset
    val h5: Int = offset
    val h6: Int = offset
    val subtitle1: Int = offset
    val subtitle2: Int = offset
    val body1: Int = offset
    val body2: Int = offset
    val button: Int = offset
    val caption: Int = offset
    val overline: Int = offset
}