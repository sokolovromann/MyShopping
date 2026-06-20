package ru.sokolovromann.myshopping.core.data.datasource

import androidx.datastore.preferences.core.stringPreferencesKey

object GeneralPreferencesScheme {

    const val FILE_NAME = "general_preferences"
    val THEME_KEY = stringPreferencesKey("theme")
    val FONT_SIZE_KEY = stringPreferencesKey("font_size")
    val DATE_TIME_FORMATTING_MODE_KEY = stringPreferencesKey("date_time_formatting_mode")
    val IS_24_HOUR_TIME_FORMAT_KEY = stringPreferencesKey("is_24_hour_time_format")
    val MONEY_FORMATTING_MODE_KEY = stringPreferencesKey("money_formatting_mode")
    val CURRENCY_KEY = stringPreferencesKey("currency")
    val CURRENCY_DISPLAY_SIDE_KEY = stringPreferencesKey("currency_display_side")
    val KEYBOARD_DISPLAY_DELAY_KEY = stringPreferencesKey("keyboard_display_delay")
}