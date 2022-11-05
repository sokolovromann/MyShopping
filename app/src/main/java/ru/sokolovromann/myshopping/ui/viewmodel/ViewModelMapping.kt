package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.getDisplayDateAndTime
import ru.sokolovromann.myshopping.ui.theme.AppColor
import ru.sokolovromann.myshopping.ui.theme.AppTypography
import java.util.Calendar
import javax.inject.Inject

class ViewModelMapping @Inject constructor() {

    fun toTextFieldValue(value: Long): TextFieldValue {
        val text = value.toString()
        return TextFieldValue(
            text = text,
            selection = TextRange(text.length),
            composition = TextRange(text.length)
        )
    }

    fun toTextFieldValue(value: Int): TextFieldValue {
        val text = value.toString()
        return TextFieldValue(
            text = text,
            selection = TextRange(text.length),
            composition = TextRange(text.length)
        )
    }

    fun toTextFieldValue(value: Float): TextFieldValue {
        val text = value.toString()
        return TextFieldValue(
            text = text,
            selection = TextRange(text.length),
            composition = TextRange(text.length)
        )
    }

    fun toTextFieldValue(value: String): TextFieldValue {
        return TextFieldValue(
            text = value,
            selection = TextRange(value.length),
            composition = TextRange(value.length)
        )
    }

    fun toLong(textFieldValue: TextFieldValue): Long? {
        return textFieldValue.text.toLongOrNull()
    }

    fun toLong(uiText: UiText): Long? {
        return if (uiText is UiText.FromLong) uiText.value else null
    }

    fun toInt(textFieldValue: TextFieldValue): Int? {
        return textFieldValue.text.toIntOrNull()
    }

    fun toInt(uiText: UiText): Int? {
        return if (uiText is UiText.FromInt) uiText.value else null
    }

    fun toFloat(textFieldValue: TextFieldValue): Float? {
        return textFieldValue.text.toFloatOrNull()
    }

    fun toFloat(uiText: UiText): Float? {
        return if (uiText is UiText.FromFloat) uiText.value else null
    }

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

    fun toDp(fontSize: FontSize, type: FontSizeType): Dp {
        return when (type) {
            FontSizeType.Header -> when (fontSize) {
                FontSize.TINY -> 18.dp
                FontSize.SMALL -> 22.dp
                FontSize.MEDIUM -> 24.dp
                FontSize.LARGE -> 28.dp
                FontSize.HUGE -> 32.dp
            }

            FontSizeType.Title -> when (fontSize) {
                FontSize.TINY -> 14.dp
                FontSize.SMALL -> 16.dp
                FontSize.MEDIUM -> 18.dp
                FontSize.LARGE -> 20.dp
                FontSize.HUGE -> 22.dp
            }

            FontSizeType.Body -> when (fontSize) {
                FontSize.TINY -> 12.dp
                FontSize.SMALL -> 14.dp
                FontSize.MEDIUM -> 16.dp
                FontSize.LARGE -> 18.dp
                FontSize.HUGE -> 20.dp
            }
        }
    }

    fun toUiText(value: Long): UiText {
        return UiText.FromLong(value)
    }

    fun toUiText(value: Int): UiText {
        return UiText.FromInt(value)
    }

    fun toUiText(value: Float): UiText {
        return UiText.FromFloat(value)
    }

    fun toUiText(value: String): UiText {
        return UiText.FromString(value)
    }

    fun toUiText(value: String, firstLetterUppercase: Boolean): UiText {
        val formatValue = value.formatFirst(firstLetterUppercase)
        return UiText.FromString(formatValue)
    }

    fun toUiTextOrNothing(value: String, firstLetterUppercase: Boolean): UiText {
        if (value.isEmpty()) {
            return UiText.Nothing
        }

        val formatValue = value.formatFirst(firstLetterUppercase)
        return UiText.FromString(formatValue)
    }

    fun toResourcesUiText(@StringRes id: Int): UiText {
        return UiText.FromResources(id)
    }

    fun toResourcesUiText(@StringRes id: Int, vararg args: Any): UiText {
        return UiText.FromResourcesWithArgs(id, args)
    }

    fun toUiIcon(imageVector: ImageVector): UiIcon {
        return UiIcon.FromVector(imageVector)
    }

    fun toUiIcon(@DrawableRes id: Int): UiIcon {
        return UiIcon.FromResources(id)
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

    fun toOnBackgroundSwitch(checked: Boolean) = SwitchData(
        checked = checked,
        checkedThumbColor = ColorData(appColor = AppColor.Secondary),
        checkedTrackColor = ColorData(appColor = AppColor.OnBackground),
        uncheckedThumbColor = ColorData(appColor = AppColor.Background),
        uncheckedTrackColor = ColorData(appColor = AppColor.OnBackground)
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

    fun toOnNavigationDrawerHeader(
        text: UiText = UiText.FromResources(R.string.route_header),
        fontSize: FontSize = FontSize.MEDIUM
    ) = TextData(
        text = text,
        style = AppTypography.H5.textStyle,
        color = ColorData(appColor = AppColor.OnBackground),
        fontSize = toTextUnit(fontSize, FontSizeType.Header)
    )

    fun toOnDialogHeader(
        text: UiText,
        fontSize: FontSize,
        appColor: AppColor = AppColor.OnSurface
    ) = TextData(
        text = text,
        style = AppTypography.H5.textStyle,
        color = ColorData(appColor = appColor),
        fontSize = toTextUnit(fontSize, FontSizeType.Header)
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

    fun toOnSurfaceIcon(icon: UiIcon, size: Dp = Dp.Unspecified) = IconData(
        icon = icon,
        size = size,
        tint = ColorData(
            appColor = AppColor.OnSurface,
            alpha = 0.7f
        )
    )

    fun toOnBackgroundIcon(icon: UiIcon, size: Dp = Dp.Unspecified) = IconData(
        icon = icon,
        size = size,
        tint = ColorData(
            appColor = AppColor.OnBackground,
            alpha = 0.7f
        )
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

    fun toOnBottomAppBar(icon: UiIcon, size: Dp = Dp.Unspecified) = IconData(
        icon = icon,
        size = size,
        tint = ColorData(
            appColor = AppColor.OnBackground,
            alpha = 0.7f
        )
    )

    fun toOnFloatingActionButton(icon: UiIcon, size: Dp = Dp.Unspecified) = IconData(
        icon = icon,
        size = size,
        tint = ColorData(appColor = AppColor.OnSecondary)
    )

    fun toRouteItem(text: UiText, icon: UiIcon, checked: Boolean) = RouteItemData(
        name = toBody(
            text = text,
            fontSize = FontSize.MEDIUM,
            appColor = if (checked) AppColor.Secondary else AppColor.OnSurface
        ),
        icon = IconData(
            icon = icon,
            tint = if (checked) {
                ColorData(appColor = AppColor.Secondary)
            } else {
                ColorData(
                    appColor = AppColor.OnSurface,
                    alpha = 0.7f
                )
            }
        ),
        checked = checked
    )

    fun toNavigationDrawerItems(checked: UiRoute): List<RouteItemData> = listOf(
        toRouteItem(
            text = toResourcesUiText(R.string.route_purchasesName),
            icon = toUiIcon(R.drawable.ic_all_purchases),
            checked = checked == UiRoute.Purchases
        ),
        toRouteItem(
            text = toResourcesUiText(R.string.route_archiveName),
            icon = toUiIcon(R.drawable.ic_all_archive),
            checked = checked == UiRoute.Archive
        ),
        toRouteItem(
            text = toResourcesUiText(R.string.route_trashName),
            icon = toUiIcon(Icons.Default.Delete),
            checked = checked == UiRoute.Trash
        ),
        toRouteItem(
            text = toResourcesUiText(R.string.route_autocompletesName),
            icon = toUiIcon(Icons.Default.List),
            checked = checked == UiRoute.Autocompletes
        ),
        toRouteItem(
            text = toResourcesUiText(R.string.route_settingsName),
            icon = toUiIcon(Icons.Default.Settings),
            checked = checked == UiRoute.Settings
        )
    )
}