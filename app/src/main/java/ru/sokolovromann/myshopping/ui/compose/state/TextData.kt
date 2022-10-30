package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.ui.theme.AppColor
import ru.sokolovromann.myshopping.ui.theme.AppTypography

data class TextData(
    val text: UiText = UiText.Nothing,
    val style: TextStyle = AppTypography.Body2.textStyle,
    val color: ColorData = ColorData(appColor = AppColor.OnSurface),
    val fontSize: FontSize = FontSize.MEDIUM,
    val fontWeight: FontWeight? = null,
    val overflow: TextOverflow = TextOverflow.Visible,
    val maxLines: Int = Int.MAX_VALUE,
    val enabled: Boolean = true
) {

    companion object {
        val Header: TextData = TextData(
            style = AppTypography.H5.textStyle,
        )

        val Title: TextData = TextData(
            style = AppTypography.Subtitle1.textStyle,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )

        val Body: TextData = TextData(
            style = AppTypography.Body1.textStyle,
            color = ColorData(appColor = AppColor.OnSurface, alpha = 0.7f),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
    }
}