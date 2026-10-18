package ru.sokolovromann.myshopping.feature.settings.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.Currency
import ru.sokolovromann.myshopping.core.domain.model.DateTimeFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.FontSize
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.model.KeyboardDisplayDelay
import ru.sokolovromann.myshopping.core.domain.model.MoneyFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.Theme
import ru.sokolovromann.myshopping.core.ui.model.UiText
import ru.sokolovromann.myshopping.feature.settings.R
import ru.sokolovromann.myshopping.feature.settings.model.GeneralSettingsItems
import ru.sokolovromann.myshopping.feature.settings.model.SettingsItem

class GeneralSettingsMapper @Inject constructor() {

    fun toItems(preferences: GeneralPreferences): GeneralSettingsItems {
        val theme = SettingsItem(
            selected = preferences.theme,
            text = when (preferences.theme) {
                Theme.Default -> UiText.FromResources(R.string.settings_text_theme_default)
                Theme.Light -> UiText.FromResources(R.string.settings_text_theme_light)
                Theme.Dark -> UiText.FromResources(R.string.settings_text_theme_dark)
            }
        )
        val fontSize = SettingsItem(
            selected = preferences.fontSize,
            text = when (preferences.fontSize) {
                FontSize.Small -> UiText.FromResources(R.string.settings_text_font_size_small)
                FontSize.Medium -> UiText.FromResources(R.string.settings_text_font_size_medium)
                FontSize.Large -> UiText.FromResources(R.string.settings_text_font_size_large)
                FontSize.ExtraLarge -> UiText.FromResources(R.string.settings_text_font_size_extra_large)
                FontSize.Huge -> UiText.FromResources(R.string.settings_text_font_size_huge)
                FontSize.ExtraHuge -> UiText.FromResources(R.string.settings_text_font_size_extra_huge)
            }
        )
        val dateTime = SettingsItem(
            selected = preferences.dateTimeFormattingMode,
            text = when (val mode = preferences.dateTimeFormattingMode) {
                is DateTimeFormattingMode.DDMMMYYYY -> {
                    if (mode.is24hour) {
                        UiText.FromResources(R.string.settings_text_date_time_formatting_mode_ddmmmyyyy_24)
                    } else {
                        UiText.FromResources(R.string.settings_text_date_time_formatting_mode_ddmmmyyyy_12)
                    }
                }
                is DateTimeFormattingMode.MMMDDYYYY -> {
                    if (mode.is24hour) {
                        UiText.FromResources(R.string.settings_text_date_time_formatting_mode_mmmddyyyy_24)
                    } else {
                        UiText.FromResources(R.string.settings_text_date_time_formatting_mode_mmmddyyyy_12)
                    }
                }
                is DateTimeFormattingMode.YYYYMMMDD -> {
                    if (mode.is24hour) {
                        UiText.FromResources(R.string.settings_text_date_time_formatting_mode_yyyymmmdd_24)
                    } else {
                        UiText.FromResources(R.string.settings_text_date_time_formatting_mode_yyyymmmdd_12)
                    }
                }
            }
        )
        val money = SettingsItem(
            selected = preferences.moneyFormattingMode,
            text = when (preferences.moneyFormattingMode) {
                MoneyFormattingMode.Simple ->
                    UiText.FromResources(R.string.settings_text_money_formatting_mode_simple)
                MoneyFormattingMode.Advanced ->
                    UiText.FromResources(R.string.settings_text_money_formatting_mode_advanced)
            }
        )
        val currency = SettingsItem(
            selected = preferences.currency,
            text = when (val currency = preferences.currency) {
                is Currency.Left -> {
                    UiText.FromResourcesWithArgs(
                        R.string.settings_text_currency_left_args,
                        currency.currencySign
                    )
                }
                is Currency.Right -> {
                    UiText.FromResourcesWithArgs(
                        R.string.settings_text_currency_right_args,
                        currency.currencySign
                    )
                }
            }
        )
        val keyboardDisplayDelay = SettingsItem(
            selected = preferences.keyboardDisplayDelay,
            text = when (preferences.keyboardDisplayDelay) {
                KeyboardDisplayDelay.Ms50 ->
                    UiText.FromResources(R.string.settings_text_keyboard_display_delay_50ms)
                KeyboardDisplayDelay.Off ->
                    UiText.FromResources(R.string.settings_text_keyboard_display_delay_off)
            }
        )
        return GeneralSettingsItems(
            theme = theme,
            fontSize = fontSize,
            dateTime = dateTime,
            money = money,
            currency = currency,
            keyboardDisplayDelay = keyboardDisplayDelay
        )
    }
}