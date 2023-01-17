package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.theme.AppColor
import ru.sokolovromann.myshopping.ui.theme.AppTypography
import javax.inject.Inject

class ViewModelMapping @Inject constructor() {
    fun toSettingsHeader(
        @StringRes header: Int,
        preferences: SettingsPreferences
    ) : TextData {
        return TextData(
            text = toResourcesUiText(header),
            style = AppTypography.Subtitle2.textStyle,
            color = ColorData(appColor = AppColor.OnSurface),
            fontSize = toTextUnit(preferences.fontSize, FontSizeType.Body),
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }

    fun toGeneralSettingsItems(settings: Settings): List<SettingsItem> {
        val values = settings.settingsValues
        val preferences = settings.preferences

        return listOf(
            SettingsItem(
                uid = SettingsUid.NightTheme,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_nightTheme),
                    fontSize = preferences.fontSize
                ),
                checked = toOnSurfaceSwitch(values.nightTheme)
            ),
            SettingsItem(
                uid = SettingsUid.FontSize,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_header_fontSize),
                    fontSize =  preferences.fontSize
                ),
                body = toBody(
                    text = toFontSizeText(values.fontSize),
                    fontSize =  preferences.fontSize
                )
            ),
            SettingsItem(
                uid = SettingsUid.FirstLetterUppercase,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_firstLetterUppercaseTitle),
                    fontSize =  preferences.fontSize
                ),
                body = toBody(
                    text = toResourcesUiText(R.string.settings_firstLetterUppercaseBody),
                    fontSize =  preferences.fontSize
                ),
                checked = toOnSurfaceSwitch(values.firstLetterUppercase)
            ),
        )
    }

    fun toMoneySettingsItems(settings: Settings): List<SettingsItem> {
        val values = settings.settingsValues
        val preferences = settings.preferences

        return listOf(
            SettingsItem(
                uid = SettingsUid.DisplayMoney,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_displayMoney),
                    fontSize = preferences.fontSize
                ),
                checked = toOnSurfaceSwitch(values.displayMoney)
            ),
            SettingsItem(
                uid = SettingsUid.Currency,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_currencySymbol),
                    fontSize = preferences.fontSize
                ),
                body = toBody(
                    text = toUiTextOrNothing(values.currency.symbol),
                    fontSize = preferences.fontSize
                )
            ),
            SettingsItem(
                uid = SettingsUid.DisplayCurrencyToLeft,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_displayCurrencySymbolToLeft),
                    fontSize = preferences.fontSize
                ),
                checked = toOnSurfaceSwitch(values.currency.displayToLeft)
            ),
            SettingsItem(
                uid = SettingsUid.TaxRate,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_taxRate),
                    fontSize = preferences.fontSize
                ),
                body = toBody(
                    text = toUiText(values.taxRate.toString()),
                    fontSize = preferences.fontSize
                )
            )
        )
    }

    fun toPurchasesSettingsItems(settings: Settings): List<SettingsItem> {
        val values = settings.settingsValues
        val preferences = settings.preferences

        return listOf(
            SettingsItem(
                uid = SettingsUid.ShoppingsMultiColumns,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_shoppingsMultiColumns),
                    fontSize = preferences.fontSize
                ),
                checked = toOnSurfaceSwitch(values.shoppingsMultiColumns)
            ),
            SettingsItem(
                uid = SettingsUid.ProductsMultiColumns,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_productsMultiColumns),
                    fontSize = preferences.fontSize
                ),
                checked = toOnSurfaceSwitch(values.productsMultiColumns)
            ),
            SettingsItem(
                uid = SettingsUid.DisplayAutocomplete,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_header_displayAutocomplete),
                    fontSize = preferences.fontSize
                ),
                body = toBody(
                    text = toDisplayAutocompleteText(values.productsDisplayAutocomplete),
                    fontSize = preferences.fontSize
                )
            ),
            SettingsItem(
                uid = SettingsUid.EditCompleted,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_editCompletedProductTitle),
                    fontSize = preferences.fontSize
                ),
                body = toBody(
                    text = toResourcesUiText(R.string.settings_editCompletedProductBody),
                    fontSize = preferences.fontSize
                ),
                checked = toOnSurfaceSwitch(values.productsEditCompleted)
            ),
            SettingsItem(
                uid = SettingsUid.AddProduct,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_addLastProductTitle),
                    fontSize = preferences.fontSize
                ),
                body = toBody(
                    text = toResourcesUiText(R.string.settings_addLastProductBody),
                    fontSize = preferences.fontSize
                ),
                checked = toOnSurfaceSwitch(values.productsAddLastProduct)
            )
        )
    }

    fun toAboutSettingsItems(settings: Settings): List<SettingsItem> {
        val values = settings.settingsValues
        val preferences = settings.preferences

        return listOf(
            SettingsItem(
                uid = SettingsUid.NoUId,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_developer),
                    fontSize = preferences.fontSize
                ),
                body = toBody(
                    text = toUiText(values.developerName),
                    fontSize = preferences.fontSize
                )
            ),
            SettingsItem(
                uid = SettingsUid.Email,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_emailTitle),
                    fontSize = preferences.fontSize
                ),
                body = toBody(
                    text = toResourcesUiText(R.string.settings_emailBody),
                    fontSize = preferences.fontSize
                )
            ),
            SettingsItem(
                uid = SettingsUid.NoUId,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_appVersion),
                    fontSize = preferences.fontSize
                ),
                body = toBody(
                    text = toUiText(values.appVersion),
                    fontSize = preferences.fontSize
                )
            ),
            SettingsItem(
                uid = SettingsUid.Github,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_githubTitle),
                    fontSize = preferences.fontSize
                ),
                body = toBody(
                    text = toResourcesUiText(R.string.settings_githubBody),
                    fontSize = preferences.fontSize
                )
            )
        )
    }

    fun toFontSizeText(fontSize: FontSize): UiText {
        return when (fontSize) {
            FontSize.TINY -> toResourcesUiText(R.string.settings_action_selectTinyFontSize)
            FontSize.SMALL -> toResourcesUiText(R.string.settings_action_selectSmallFontSize)
            FontSize.MEDIUM -> toResourcesUiText(R.string.settings_action_selectMediumFontSize)
            FontSize.LARGE -> toResourcesUiText(R.string.settings_action_selectLargeFontSize)
            FontSize.HUGE -> toResourcesUiText(R.string.settings_action_selectHugeFontSize)
        }
    }

    fun toDisplayAutocompleteText(displayAutocomplete: DisplayAutocomplete): UiText {
        return when (displayAutocomplete) {
            DisplayAutocomplete.ALL -> toResourcesUiText(R.string.settings_action_displayAllAutocomplete)
            DisplayAutocomplete.NAME -> toResourcesUiText(R.string.settings_action_displayNameAutocomplete)
            DisplayAutocomplete.HIDE -> toResourcesUiText(R.string.settings_action_selectHideAutocomplete)
        }
    }

    fun toFontSizeMenu(fontSize: FontSize): FontSizeMenu {
        return FontSizeMenu(
            tinyBody = toBody(
                text = toResourcesUiText(R.string.settings_action_selectTinyFontSize),
                fontSize = fontSize
            ),
            tinySelected = toRadioButton(selected = fontSize == FontSize.TINY),
            smallBody = toBody(
                text = toResourcesUiText(R.string.settings_action_selectSmallFontSize),
                fontSize = fontSize
            ),
            smallSelected = toRadioButton(selected = fontSize == FontSize.SMALL),
            mediumBody = toBody(
                text = toResourcesUiText(R.string.settings_action_selectMediumFontSize),
                fontSize = fontSize
            ),
            mediumSelected = toRadioButton(selected = fontSize == FontSize.MEDIUM),
            largeBody = toBody(
                text = toResourcesUiText(R.string.settings_action_selectLargeFontSize),
                fontSize = fontSize
            ),
            largeSelected = toRadioButton(selected = fontSize == FontSize.LARGE),
            hugeBody = toBody(
                text = toResourcesUiText(R.string.settings_action_selectHugeFontSize),
                fontSize = fontSize
            ),
            hugeSelected = toRadioButton(selected = fontSize == FontSize.HUGE)
        )
    }

    fun toDisplayAutocompleteMenu(
        displayAutocomplete: DisplayAutocomplete,
        fontSize: FontSize
    ): DisplayAutocompleteMenu {
        return DisplayAutocompleteMenu(
            allBody = toBody(
                text = toResourcesUiText(R.string.settings_action_displayAllAutocomplete),
                fontSize = fontSize
            ),
            allSelected = toRadioButton(selected = displayAutocomplete == DisplayAutocomplete.ALL),
            nameBody = toBody(
                text = toResourcesUiText(R.string.settings_action_displayNameAutocomplete),
                fontSize = fontSize
            ),
            nameSelected = toRadioButton(selected = displayAutocomplete == DisplayAutocomplete.NAME),
            hideBody = toBody(
                text = toResourcesUiText(R.string.settings_action_selectHideAutocomplete),
                fontSize = fontSize
            ),
            hideSelected = toRadioButton(selected = displayAutocomplete == DisplayAutocomplete.HIDE)
        )
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