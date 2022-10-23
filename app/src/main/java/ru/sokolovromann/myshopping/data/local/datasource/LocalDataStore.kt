package ru.sokolovromann.myshopping.data.local.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import ru.sokolovromann.myshopping.AppDispatchers
import javax.inject.Inject

class LocalDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val dispatchers: AppDispatchers
) {

    companion object {
        const val DATASTORE_NAME = "local_datastore"
    }

    private val appOpenedActionKey = stringPreferencesKey("app_opened_action")
    private val nightThemeKey = booleanPreferencesKey("night_theme")
    private val currencyKey = stringPreferencesKey("currency")
    private val currencyDisplayToLeftKey = booleanPreferencesKey("currency_display_to_left")
    private val taxRateKey = floatPreferencesKey("tax_rate")
    private val taxRateAsPercentKey = booleanPreferencesKey("tax_rate_as_percent")
    private val fontSizeKey = stringPreferencesKey("font_size")
    private val firstLetterUppercaseKey = booleanPreferencesKey("first_letter_uppercase")
    private val shoppingsMultiColumnsKey = booleanPreferencesKey("shoppings_multi_columns")
    private val shoppingsSortByKey = stringPreferencesKey("shoppings_sort_by")
    private val shoppingsSortAscendingKey = booleanPreferencesKey("shoppings_sort_ascending")
    private val shoppingsDisplayCompletedKey = stringPreferencesKey("shoppings_display_completed")
    private val shoppingsDisplayTotalKey = stringPreferencesKey("shoppings_display_total")
    private val productsMultiColumnsKey = booleanPreferencesKey("products_multi_columns")
    private val productsSortByKey = stringPreferencesKey("products_sort_by")
    private val productsSortAscendingKey = booleanPreferencesKey("products_sort_ascending")
    private val productsDisplayCompletedKey = stringPreferencesKey("products_display_completed")
    private val productsDisplayTotalKey = stringPreferencesKey("products_display_total")
    private val productsLockQuantityKey = booleanPreferencesKey("products_lock_quantity")
    private val productsEditCompletedKey = booleanPreferencesKey("products_edit_completed")
    private val productsAddLastProductKey = booleanPreferencesKey("products_add_last_product")
    private val displayMoneyKey = booleanPreferencesKey("display_money")
    private val screenSizeKey = stringPreferencesKey("screen_size")
}