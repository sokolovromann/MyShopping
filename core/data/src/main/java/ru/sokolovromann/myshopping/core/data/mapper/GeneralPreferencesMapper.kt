package ru.sokolovromann.myshopping.core.data.mapper

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import ru.sokolovromann.myshopping.core.data.datasource.GeneralPreferencesScheme
import ru.sokolovromann.myshopping.core.domain.model.Currency
import ru.sokolovromann.myshopping.core.domain.model.DateTimeFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.FontSize
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.model.KeyboardDisplayDelay
import ru.sokolovromann.myshopping.core.domain.model.MoneyFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.Theme
import ru.sokolovromann.myshopping.core.domain.utils.EnumUtils
import kotlin.text.orEmpty

class GeneralPreferencesMapper : DataStoreMapper<GeneralPreferences>() {

    override fun toModel(preferences: Preferences) = GeneralPreferences(
        EnumUtils.valueOfOrDefault(
            preferences[GeneralPreferencesScheme.THEME_KEY],
            Theme.Default
        ),
        EnumUtils.valueOfOrDefault(
            preferences[GeneralPreferencesScheme.FONT_SIZE_KEY],
            FontSize.Medium
        ),
        toDateTimeFormattingMode(
            preferences[GeneralPreferencesScheme.DATE_TIME_FORMATTING_MODE_KEY],
            preferences[GeneralPreferencesScheme.IS_24_HOUR_TIME_FORMAT_KEY]
        ),
        EnumUtils.valueOfOrDefault(
            preferences[GeneralPreferencesScheme.MONEY_FORMATTING_MODE_KEY],
            MoneyFormattingMode.Simple
        ),
        toCurrency(
            preferences[GeneralPreferencesScheme.CURRENCY_KEY],
            preferences[GeneralPreferencesScheme.CURRENCY_DISPLAY_SIDE_KEY]
        ),
        EnumUtils.valueOfOrDefault(
            preferences[GeneralPreferencesScheme.KEYBOARD_DISPLAY_DELAY_KEY],
            KeyboardDisplayDelay.Off
        )
    )

    override fun toPreferences(model: GeneralPreferences) = preferencesOf(
        GeneralPreferencesScheme.THEME_KEY to model.theme.toString(),
        GeneralPreferencesScheme.FONT_SIZE_KEY to model.fontSize.toString(),
        GeneralPreferencesScheme.DATE_TIME_FORMATTING_MODE_KEY to model.dateTimeFormattingMode.javaClass.simpleName,
        GeneralPreferencesScheme.IS_24_HOUR_TIME_FORMAT_KEY to model.dateTimeFormattingMode.is24HourFormat().toString(),
        GeneralPreferencesScheme.MONEY_FORMATTING_MODE_KEY to model.moneyFormattingMode.toString(),
        GeneralPreferencesScheme.CURRENCY_KEY to model.currency.getSign(),
        GeneralPreferencesScheme.CURRENCY_DISPLAY_SIDE_KEY to model.currency.javaClass.simpleName,
        GeneralPreferencesScheme.KEYBOARD_DISPLAY_DELAY_KEY to model.keyboardDisplayDelay.toString()
    )

    private fun toDateTimeFormattingMode(
        formattingMode: String?,
        is24HourFormat: String?
    ): DateTimeFormattingMode {
        val is24hour = is24HourFormat.toBoolean()
        return when (formattingMode) {
            "DDMMMYYYY" -> DateTimeFormattingMode.DDMMMYYYY(is24hour)
            "MMMDDYYYY" -> DateTimeFormattingMode.MMMDDYYYY(is24hour)
            "YYYYMMMDD" -> DateTimeFormattingMode.YYYYMMMDD(is24hour)
            else -> DateTimeFormattingMode.DDMMMYYYY(is24hour)
        }
    }

    private fun toCurrency(sign: String?, displaySide: String?): Currency {
        val currencySign = sign.orEmpty()
        return when (displaySide) {
            "Left" -> Currency.Left(currencySign)
            "Right" -> Currency.Right(currencySign)
            else -> Currency.Right(currencySign)
        }
    }
}