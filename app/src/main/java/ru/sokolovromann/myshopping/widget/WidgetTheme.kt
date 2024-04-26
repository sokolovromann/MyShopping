package ru.sokolovromann.myshopping.widget

import androidx.compose.ui.unit.sp
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle

fun createWidgetTypography(
    fontSizeOffset: WidgetFontSizeOffset = WidgetFontSizeOffset()
): WidgetTypography {
    return WidgetTypography(
        title = TextStyle(
            fontSize = (18 + fontSizeOffset.title).sp,
            fontWeight = FontWeight.Bold
        ),
        body = TextStyle(
            fontSize = (16 + fontSizeOffset.body).sp,
            fontWeight = FontWeight.Normal
        )
    )
}

data class WidgetTypography(
    val title: TextStyle,
    val body: TextStyle
)

data class WidgetFontSizeOffset(val offset: Int = 0) {
    val title: Int = offset
    val body: Int = offset
}
