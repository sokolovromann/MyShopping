package ru.sokolovromann.myshopping.data.local.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.entity.*
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
    private val fontSizeKey = stringPreferencesKey("font_size")
    private val smartphoneScreenKey = booleanPreferencesKey("screen_size")
    private val currencyKey = stringPreferencesKey("currency")
    private val displayCurrencyToLeftKey = booleanPreferencesKey("display_currency_to_left")
    private val taxRateKey = floatPreferencesKey("tax_rate")
    private val taxRateAsPercentKey = booleanPreferencesKey("tax_rate_as_percent")
    private val shoppingsMultiColumnsKey = booleanPreferencesKey("shoppings_multi_columns")
    private val productsMultiColumnsKey = booleanPreferencesKey("products_multi_columns")
    private val displayCompletedPurchasesKey = stringPreferencesKey("display_completed_purchases")
    private val displayPurchasesTotalKey = stringPreferencesKey("display_purchases_total")
    private val editProductAfterCompletedKey = booleanPreferencesKey("edit_product_after_completed")
    private val saveProductToAutocompletesKey = booleanPreferencesKey("save_product_to_autocompletes")
    private val lockProductElementKey = stringPreferencesKey("lock_product_element")
    private val displayMoneyKey = booleanPreferencesKey("display_money")
    private val displayDefaultAutocompletesKey = booleanPreferencesKey("display_default_autocompletes")

    suspend fun getAppPreferences(): Flow<AppPreferencesEntity> = withContext(dispatchers.io) {
        return@withContext dataStore.data.map {
            AppPreferencesEntity(
                appOpenedAction = it[appOpenedActionKey] ?: "",
                nightTheme = it[nightThemeKey] ?: false,
                fontSize = it[fontSizeKey] ?: "",
                smartphoneScreen = it[smartphoneScreenKey] ?: true,
                currency = it[currencyKey] ?: "",
                displayCurrencyToLeft = it[displayCurrencyToLeftKey] ?: false,
                taxRate = it[taxRateKey] ?: 0f,
                taxRateAsPercent = it[taxRateAsPercentKey] ?: true,
                shoppingsMultiColumns = it[shoppingsMultiColumnsKey] ?: false,
                productsMultiColumns = it[productsMultiColumnsKey] ?: false,
                displayCompletedPurchases = it[displayCompletedPurchasesKey] ?: "",
                displayPurchasesTotal = it[displayPurchasesTotalKey] ?: "",
                editProductAfterCompleted = it[editProductAfterCompletedKey] ?: false,
                saveProductToAutocompletes = it[saveProductToAutocompletesKey] ?: true,
                lockProductElement = it[lockProductElementKey] ?: "",
                displayMoney = it[displayMoneyKey] ?: true,
                displayDefaultAutocompletes = it[displayDefaultAutocompletesKey] ?: true
            )
        }
    }

    suspend fun saveAppPreferences(entity: AppPreferencesEntity) = withContext(dispatchers.io) {
        dataStore.edit {
            it[appOpenedActionKey] = entity.appOpenedAction
            it[nightThemeKey] = entity.nightTheme
            it[fontSizeKey] = entity.fontSize
            it[smartphoneScreenKey] = entity.smartphoneScreen
            it[currencyKey] = entity.currency
            it[displayCurrencyToLeftKey] = entity.displayCurrencyToLeft
            it[taxRateKey] = entity.taxRate
            it[taxRateAsPercentKey] = entity.taxRateAsPercent
            it[shoppingsMultiColumnsKey] = entity.shoppingsMultiColumns
            it[productsMultiColumnsKey] = entity.productsMultiColumns
            it[displayCompletedPurchasesKey] = entity.displayCompletedPurchases
            it[displayPurchasesTotalKey] = entity.displayPurchasesTotal
            it[editProductAfterCompletedKey] = entity.editProductAfterCompleted
            it[saveProductToAutocompletesKey] = entity.saveProductToAutocompletes
            it[lockProductElementKey] = entity.lockProductElement
            it[displayMoneyKey] = entity.displayMoney
            it[displayDefaultAutocompletesKey] = entity.displayDefaultAutocompletes
        }
    }

    suspend fun saveFontSize(fontSize: String) = withContext(dispatchers.io) {
        dataStore.edit { it[fontSizeKey] = fontSize }
    }

    suspend fun saveCurrency(currency: String) = withContext(dispatchers.io) {
        dataStore.edit { it[currencyKey] = currency }
    }

    suspend fun saveTaxRate(taxRate: Float) = withContext(dispatchers.io) {
        dataStore.edit { it[taxRateKey] = taxRate }
    }

    suspend fun saveTaxRateAsPercent(asPercent: Boolean) = withContext(dispatchers.io) {
        dataStore.edit { it[taxRateAsPercentKey] = asPercent }
    }

    suspend fun displayCompletedPurchases(displayCompleted: String) = withContext(dispatchers.io) {
        dataStore.edit { it[displayCompletedPurchasesKey] = displayCompleted }
    }

    suspend fun displayPurchasesTotal(displayTotal: String) = withContext(dispatchers.io) {
        dataStore.edit { it[displayPurchasesTotalKey] = displayTotal }
    }

    suspend fun lockProductElement(lockProductElement: String) = withContext(dispatchers.io) {
        dataStore.edit { it[lockProductElementKey] = lockProductElement }
    }

    suspend fun invertNightTheme() = withContext(dispatchers.io) {
        dataStore.edit {
            val nightTheme = it[nightThemeKey] ?: false
            it[nightThemeKey] = !nightTheme
        }
    }

    suspend fun invertDisplayCurrencyToLeft() = withContext(dispatchers.io) {
        dataStore.edit {
            val displayToLeft = it[displayCurrencyToLeftKey] ?: false
            it[displayCurrencyToLeftKey] = !displayToLeft
        }
    }

    suspend fun invertShoppingsMultiColumns() = withContext(dispatchers.io) {
        dataStore.edit {
            val multiColumns = it[shoppingsMultiColumnsKey] ?: false
            it[shoppingsMultiColumnsKey] = !multiColumns
        }
    }

    suspend fun invertProductsMultiColumns() = withContext(dispatchers.io) {
        dataStore.edit {
            val multiColumns = it[productsMultiColumnsKey] ?: false
            it[productsMultiColumnsKey] = !multiColumns
        }
    }

    suspend fun invertEditProductAfterCompleted() = withContext(dispatchers.io) {
        dataStore.edit {
            val editProduct = it[editProductAfterCompletedKey] ?: false
            it[editProductAfterCompletedKey] = !editProduct
        }
    }

    suspend fun invertSaveProductToAutocompletes() = withContext(dispatchers.io) {
        dataStore.edit {
            val saveProduct = it[saveProductToAutocompletesKey] ?: false
            it[saveProductToAutocompletesKey] = !saveProduct
        }
    }

    suspend fun invertDisplayMoney() = withContext(dispatchers.io) {
        dataStore.edit {
            val displayMoney = it[displayMoneyKey] ?: false
            it[displayMoneyKey] = !displayMoney
        }
    }

    suspend fun invertDisplayDefaultAutocompletes() = withContext(dispatchers.io) {
        dataStore.edit {
            val displayAutocomplete = it[displayDefaultAutocompletesKey] ?: true
            it[displayDefaultAutocompletesKey] = !displayAutocomplete
        }
    }
}