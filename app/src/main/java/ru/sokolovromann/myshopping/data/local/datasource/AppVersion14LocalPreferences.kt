package ru.sokolovromann.myshopping.data.local.datasource

import android.content.Context
import android.content.SharedPreferences
import ru.sokolovromann.myshopping.data.local.entity.AppVersion14PreferencesEntity
import javax.inject.Inject

class AppVersion14LocalPreferences @Inject constructor(context: Context) {

    private val settingsPreferences: SharedPreferences = context
        .getSharedPreferences("MyPref", Context.MODE_PRIVATE)

    private val firstOpenedPreferences: SharedPreferences = context
        .getSharedPreferences("First", Context.MODE_PRIVATE)

    fun getAppVersion14Preferences(): AppVersion14PreferencesEntity {
        return AppVersion14PreferencesEntity(
            firstOpened = firstOpenedPreferences.getBoolean("pref_first", false),
            currency = settingsPreferences.getString("currency", "") ?: "",
            displayCurrencyToLeft = settingsPreferences.getBoolean("show_currency", false),
            taxRate = settingsPreferences.getFloat("tax_rate", 0f),
            titleFontSize = settingsPreferences.getInt("size_main_text", 18),
            bodyFontSize = settingsPreferences.getInt("size_dop_text", 16),
            firstLetterUppercase = settingsPreferences.getBoolean("capital_text", true),
            columnCount = settingsPreferences.getInt("cell_text", 1),
            sort = settingsPreferences.getInt("sort_default", 0),
            displayMoney = settingsPreferences.getBoolean("show_price", true),
            displayTotal = settingsPreferences.getInt("sum_default", 0),
            editProductAfterCompleted = settingsPreferences.getBoolean("edit_after_buy", false),
            saveProductToAutocompletes = settingsPreferences.getBoolean("auto_text", true)
        )
    }

    fun isMigrateFromAppVersion14(): Boolean {
        return firstOpenedPreferences.getBoolean("pref_first", false)
    }
}