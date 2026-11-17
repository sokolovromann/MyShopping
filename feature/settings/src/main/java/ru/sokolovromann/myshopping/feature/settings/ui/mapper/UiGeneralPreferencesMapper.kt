package ru.sokolovromann.myshopping.feature.settings.ui.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.Currency
import ru.sokolovromann.myshopping.core.domain.model.FontSize
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.model.MoneyFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.Theme
import ru.sokolovromann.myshopping.core.ui.model.UiText
import ru.sokolovromann.myshopping.feature.settings.R
import ru.sokolovromann.myshopping.feature.settings.ui.model.UiGeneralPreferences
import ru.sokolovromann.myshopping.feature.settings.ui.model.UiPreference

class UiGeneralPreferencesMapper @Inject constructor() {

    fun toPreferences(preferences: GeneralPreferences): UiGeneralPreferences {
        val theme = UiPreference(
            selected = preferences.theme,
            text = when (preferences.theme) {
                Theme.Default -> UiText.FromResources(R.string.settings_text_theme_default)
                Theme.Light -> UiText.FromResources(R.string.settings_text_theme_light)
                Theme.Dark -> UiText.FromResources(R.string.settings_text_theme_dark)
            }
        )
        val fontSize = UiPreference(
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
        val money = UiPreference(
            selected = preferences.moneyFormattingMode,
            text = when (preferences.moneyFormattingMode) {
                MoneyFormattingMode.Simple ->
                    UiText.FromResources(R.string.settings_text_money_formatting_mode_simple)
                MoneyFormattingMode.Advanced ->
                    UiText.FromResources(R.string.settings_text_money_formatting_mode_advanced)
            }
        )
        val currency = UiPreference(
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
        return UiGeneralPreferences(theme, fontSize, money, currency)
    }
}