package ru.sokolovromann.myshopping.data39.settings.general

import androidx.datastore.preferences.core.stringPreferencesKey

object GeneralConfigScheme {
    const val DATA_STORE_NAME: String = "local_general_config"

    val THEME = stringPreferencesKey("theme")
    val FONT_SIZE = stringPreferencesKey("font_size")
    val DATE_FORMATTING_MODE = stringPreferencesKey("date_formatting_mode")
    val TIME_FORMATTING_MODE = stringPreferencesKey("time_formatting_mode")
    val MONEY_FRACTION_DIGITS = stringPreferencesKey("money_fraction_digits")
    val CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
    val CURRENCY_DISPLAY_SIDE = stringPreferencesKey("currency_display_side")
    val TAX_RATE = stringPreferencesKey("tax_rate")
}