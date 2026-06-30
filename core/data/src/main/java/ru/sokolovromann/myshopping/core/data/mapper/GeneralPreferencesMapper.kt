package ru.sokolovromann.myshopping.core.data.mapper

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import jakarta.inject.Inject
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.core.data.datasource.LocalDataStoreScheme
import ru.sokolovromann.myshopping.core.domain.model.Currency
import ru.sokolovromann.myshopping.core.domain.model.DateTimeFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.FontSize
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.model.KeyboardDisplayDelay
import ru.sokolovromann.myshopping.core.domain.model.MoneyFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.Theme
import ru.sokolovromann.myshopping.core.domain.utils.EnumUtils
import kotlin.text.orEmpty

@Singleton
class GeneralPreferencesMapper @Inject constructor() : DataStoreMapper<GeneralPreferences>() {

    override fun toModel(preferences: Preferences) = GeneralPreferences(
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.General.THEME],
            Theme.Default
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.General.FONT_SIZE],
            FontSize.Medium
        ),
        toDateTimeFormattingMode(
            preferences[LocalDataStoreScheme.General.DATE_TIME_FORMATTING_MODE],
            preferences[LocalDataStoreScheme.General.IS_24_HOUR_TIME_FORMAT]
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.General.MONEY_FORMATTING_MODE],
            MoneyFormattingMode.Simple
        ),
        toCurrency(
            preferences[LocalDataStoreScheme.General.CURRENCY],
            preferences[LocalDataStoreScheme.General.CURRENCY_DISPLAY_SIDE]
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.General.KEYBOARD_DISPLAY_DELAY],
            KeyboardDisplayDelay.Off
        )
    )

    override fun toPreferences(model: GeneralPreferences) = preferencesOf(
        LocalDataStoreScheme.General.THEME
                to model.theme.toString(),
        LocalDataStoreScheme.General.FONT_SIZE
                to model.fontSize.toString(),
        LocalDataStoreScheme.General.DATE_TIME_FORMATTING_MODE
                to model.dateTimeFormattingMode.javaClass.simpleName,
        LocalDataStoreScheme.General.IS_24_HOUR_TIME_FORMAT
                to model.dateTimeFormattingMode.is24HourFormat().toString(),
        LocalDataStoreScheme.General.MONEY_FORMATTING_MODE
                to model.moneyFormattingMode.toString(),
        LocalDataStoreScheme.General.CURRENCY
                to model.currency.getSign(),
        LocalDataStoreScheme.General.CURRENCY_DISPLAY_SIDE
                to model.currency.javaClass.simpleName,
        LocalDataStoreScheme.General.KEYBOARD_DISPLAY_DELAY
                to model.keyboardDisplayDelay.toString()
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