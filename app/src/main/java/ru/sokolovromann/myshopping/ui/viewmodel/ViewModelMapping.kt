package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.theme.AppColor
import ru.sokolovromann.myshopping.ui.theme.AppTypography
import javax.inject.Inject

class ViewModelMapping @Inject constructor() {

    fun toString(textFieldValue: TextFieldValue): String {
        return textFieldValue.text
    }

    fun toString(uiText: UiText): String {
        return if (uiText is UiText.FromString) uiText.value else ""
    }

    fun toTextUnit(fontSize: FontSize, type: FontSizeType): TextUnit {
        return when (type) {
            FontSizeType.Header -> when (fontSize) {
                FontSize.TINY -> 18.sp
                FontSize.SMALL -> 22.sp
                FontSize.MEDIUM -> 24.sp
                FontSize.LARGE -> 28.sp
                FontSize.HUGE -> 32.sp
            }

            FontSizeType.Title -> when (fontSize) {
                FontSize.TINY -> 14.sp
                FontSize.SMALL -> 16.sp
                FontSize.MEDIUM -> 18.sp
                FontSize.LARGE -> 20.sp
                FontSize.HUGE -> 22.sp
            }

            FontSizeType.Body -> when (fontSize) {
                FontSize.TINY -> 12.sp
                FontSize.SMALL -> 14.sp
                FontSize.MEDIUM -> 16.sp
                FontSize.LARGE -> 18.sp
                FontSize.HUGE -> 20.sp
            }
        }
    }

    fun toUiText(value: String): UiText {
        return UiText.FromString(value)
    }

    fun toUiTextOrNothing(value: String): UiText {
        return if (value.isEmpty()) {
             UiText.Nothing
        } else {
            UiText.FromString(value)
        }
    }

    fun toResourcesUiText(@StringRes id: Int): UiText {
        return UiText.FromResources(id)
    }

    fun toUiIcon(imageVector: ImageVector): UiIcon {
        return UiIcon.FromVector(imageVector)
    }

    fun toRadioButton(
        selected: Boolean,
        appColor: AppColor = AppColor.OnSurface
    ) = RadioButtonData(
        selected = selected,
        selectedColor = ColorData(appColor = appColor),
        unselectedColor = ColorData(appColor = appColor)
    )

    fun toOnSurfaceSwitch(checked: Boolean) = SwitchData(
        checked = checked,
        checkedThumbColor = ColorData(appColor = AppColor.Secondary),
        checkedTrackColor = ColorData(appColor = AppColor.OnSurface),
        uncheckedThumbColor = ColorData(appColor = AppColor.Surface),
        uncheckedTrackColor = ColorData(appColor = AppColor.OnSurface)
    )

    fun toOnTopAppBarHeader(text: UiText, fontSize: FontSize) = TextData(
        text = text,
        style = AppTypography.H5.textStyle,
        color = ColorData(light = AppColor.OnPrimary, dark = AppColor.OnSurface),
        fontSize = toTextUnit(fontSize, FontSizeType.Header),
        fontWeight = FontWeight.Normal,
        overflow = TextOverflow.Ellipsis,
        maxLines = 2
    )

    fun toTitle(
        text: UiText,
        fontSize: FontSize,
        appColor: AppColor = AppColor.OnSurface
    ) = TextData(
        text = text,
        style = AppTypography.Subtitle1.textStyle,
        color = ColorData(appColor = appColor),
        fontSize = toTextUnit(fontSize, FontSizeType.Title),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
    )

    fun toBody(
        text: UiText,
        fontSize: FontSize,
        appColor: AppColor = AppColor.OnSurface
    ) = TextData(
        text = text,
        style = AppTypography.Body1.textStyle,
        color = ColorData(appColor = appColor, alpha = 0.7f),
        fontSize = toTextUnit(fontSize, FontSizeType.Body),
        overflow = TextOverflow.Ellipsis,
        maxLines = 2
    )

    fun toOnTopAppBar(icon: UiIcon, size: Dp = Dp.Unspecified) = IconData(
        icon = icon,
        size = size,
        tint = ColorData(
            light = AppColor.OnPrimary,
            lightAlpha = 0.7f,
            dark = AppColor.OnSurface,
            darkAlpha = 0.7f
        )
    )
}