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
    private val currencyKey = stringPreferencesKey("currency")
    private val currencyDisplayToLeftKey = booleanPreferencesKey("currency_display_to_left")
    private val taxRateKey = floatPreferencesKey("tax_rate")
    private val taxRateAsPercentKey = booleanPreferencesKey("tax_rate_as_percent")
    private val fontSizeKey = stringPreferencesKey("font_size")
    private val firstLetterUppercaseKey = booleanPreferencesKey("first_letter_uppercase")
    private val shoppingsMultiColumnsKey = booleanPreferencesKey("shoppings_multi_columns")
    private val shoppingsDisplayTotalKey = stringPreferencesKey("shoppings_display_total")
    private val shoppingsMaxProductsKey = intPreferencesKey("shoppings_max_products")
    private val productsMultiColumnsKey = booleanPreferencesKey("products_multi_columns")
    private val productsDisplayTotalKey = stringPreferencesKey("products_display_total")
    private val productsDisplayAutocompleteKey = stringPreferencesKey("products_display_autocomplete")
    private val productsProductLockKey = stringPreferencesKey("products_product_lock")
    private val productsEditCompletedKey = booleanPreferencesKey("products_edit_completed")
    private val productsAddLastProductKey = booleanPreferencesKey("products_add_last_product")
    private val productsDisplayDefaultAutocompleteKey = booleanPreferencesKey("products_display_default_autocomplete")
    private val displayMoneyKey = booleanPreferencesKey("display_money")
    private val displayCompletedKey = stringPreferencesKey("display_completed")
    private val screenSizeKey = stringPreferencesKey("screen_size")

    suspend fun getShoppingPreferences(): Flow<ShoppingPreferencesEntity> = withContext(dispatchers.io) {
        return@withContext dataStore.data.map {
            ShoppingPreferencesEntity(
                currency = it[currencyKey] ?: "",
                currencyDisplayToLeft = it[currencyDisplayToLeftKey] ?: false,
                taxRate = it[taxRateKey] ?: 0f,
                taxRateAsPercent = it[taxRateAsPercentKey] ?: true,
                fontSize = it[fontSizeKey] ?: "",
                firstLetterUppercase = it[firstLetterUppercaseKey] ?: true,
                multiColumns = it[shoppingsMultiColumnsKey] ?: false,
                displayMoney = it[displayMoneyKey] ?: true,
                displayCompleted = it[displayCompletedKey] ?: "",
                displayTotal = it[shoppingsDisplayTotalKey] ?: "",
                screenSize = it[screenSizeKey] ?: "",
                maxProducts = it[shoppingsMaxProductsKey] ?: 10
            )
        }
    }

    suspend fun getProductPreferences(): Flow<ProductPreferencesEntity> = withContext(dispatchers.io) {
        return@withContext dataStore.data.map {
            ProductPreferencesEntity(
                currency = it[currencyKey] ?: "",
                currencyDisplayToLeft = it[currencyDisplayToLeftKey] ?: false,
                taxRate = it[taxRateKey] ?: 0f,
                taxRateAsPercent = it[taxRateAsPercentKey] ?: true,
                fontSize = it[fontSizeKey] ?: "",
                firstLetterUppercase = it[firstLetterUppercaseKey] ?: true,
                multiColumns = it[productsMultiColumnsKey] ?: false,
                displayMoney = it[displayMoneyKey] ?: true,
                displayCompleted = it[displayCompletedKey] ?: "",
                displayTotal = it[productsDisplayTotalKey] ?: "",
                displayAutocomplete = it[productsDisplayAutocompleteKey] ?: "",
                displayDefaultAutocomplete = it[productsDisplayDefaultAutocompleteKey] ?: true,
                productLock = it[productsProductLockKey] ?: "",
                editCompleted = it[productsEditCompletedKey] ?: false,
                addLastProduct = it[productsAddLastProductKey] ?: true,
                screenSize = it[screenSizeKey] ?: ""
            )
        }
    }

    suspend fun getAutocompletePreferences(): Flow<AutocompletePreferencesEntity> = withContext(dispatchers.io) {
        return@withContext dataStore.data.map {
            AutocompletePreferencesEntity(
                currency = it[currencyKey] ?: "",
                currencyDisplayToLeft = it[currencyDisplayToLeftKey] ?: false,
                fontSize = it[fontSizeKey] ?: "",
                firstLetterUppercase = it[firstLetterUppercaseKey] ?: true,
                screenSize = it[screenSizeKey] ?: ""
            )
        }
    }

    suspend fun getSettings(): Flow<SettingsEntity> = withContext(dispatchers.io) {
        return@withContext dataStore.data.map {
            SettingsEntity(
                nightTheme = it[nightThemeKey] ?: false,
                currency = it[currencyKey] ?: "",
                currencyDisplayToLeft = it[currencyDisplayToLeftKey] ?: false,
                taxRate = it[taxRateKey] ?: 0f,
                taxRateAsPercent = it[taxRateAsPercentKey] ?: true,
                fontSize = it[fontSizeKey] ?: "",
                firstLetterUppercase = it[firstLetterUppercaseKey] ?: true,
                displayMoney = it[displayMoneyKey] ?: true,
                displayCompleted = it[displayCompletedKey] ?: "",
                shoppingsMultiColumns = it[shoppingsMultiColumnsKey] ?: false,
                productsMultiColumns = it[productsMultiColumnsKey] ?: false,
                productsDisplayAutocomplete = it[productsDisplayAutocompleteKey] ?: "",
                productsEditCompleted = it[productsEditCompletedKey] ?: false,
                productsAddLastProduct = it[productsAddLastProductKey] ?: true,
                productsDisplayDefaultAutocomplete = it[productsDisplayDefaultAutocompleteKey] ?: true
            )
        }
    }

    suspend fun getSettingsPreferences(): Flow<SettingsPreferencesEntity> = withContext(dispatchers.io) {
        return@withContext dataStore.data.map {
            SettingsPreferencesEntity(
                fontSize = it[fontSizeKey] ?: "",
                screenSize = it[screenSizeKey] ?: ""
            )
        }
    }

    suspend fun getMainPreferences(): Flow<MainPreferencesEntity> = withContext(dispatchers.io) {
        return@withContext dataStore.data.map {
            MainPreferencesEntity(
                appOpenedAction = it[appOpenedActionKey] ?: "",
                nightTheme = it[nightThemeKey] ?: false
            )
        }
    }

    suspend fun getEditCurrencySymbol(): Flow<EditCurrencySymbolEntity> = withContext(dispatchers.io) {
        return@withContext dataStore.data.map {
            EditCurrencySymbolEntity(currency = it[currencyKey] ?: "")
        }
    }

    suspend fun getEditTaxRate(): Flow<EditTaxRateEntity> = withContext(dispatchers.io) {
        return@withContext dataStore.data.map {
            EditTaxRateEntity(
                taxRate = it[taxRateKey] ?: 0f,
                taxRateAsPercent = it[taxRateAsPercentKey] ?: true
            )
        }
    }

    suspend fun addMainPreferences(entity: MainPreferencesEntity) = withContext(dispatchers.io) {
        dataStore.edit {
            it[appOpenedActionKey] = entity.appOpenedAction
            it[nightThemeKey] = entity.nightTheme
            it[currencyKey] = entity.currency
            it[currencyDisplayToLeftKey] = entity.currencyDisplayToLeft
            it[taxRateKey] = entity.taxRate
            it[taxRateAsPercentKey] = entity.taxRateAsPercent
            it[fontSizeKey] = entity.fontSize
            it[firstLetterUppercaseKey] = entity.firstLetterUppercase
            it[shoppingsMultiColumnsKey] = entity.shoppingsMultiColumns
            it[shoppingsDisplayTotalKey] = entity.shoppingsDisplayTotal
            it[shoppingsMaxProductsKey] = entity.shoppingsMaxProducts
            it[productsMultiColumnsKey] = entity.productsMultiColumns
            it[productsDisplayTotalKey] = entity.productsDisplayTotal
            it[productsDisplayAutocompleteKey] = entity.productsDisplayAutocomplete
            it[productsProductLockKey] = entity.productsProductLock
            it[productsEditCompletedKey] = entity.productsEditCompleted
            it[productsAddLastProductKey] = entity.productsAddLastProduct
            it[productsDisplayDefaultAutocompleteKey] = entity.productsDisplayDefaultAutocomplete
            it[displayMoneyKey] = entity.displayMoney
            it[displayCompletedKey] = entity.displayCompleted
            it[screenSizeKey] = entity.screenSize
        }
    }

    suspend fun saveCurrency(currency: String) = withContext(dispatchers.io) {
        dataStore.edit { it[currencyKey] = currency }
    }

    suspend fun saveTaxRate(taxRate: Float) = withContext(dispatchers.io) {
        dataStore.edit { it[taxRateKey] = taxRate }
    }

    suspend fun saveTaxRateAsTaxRate(asPercent: Boolean) = withContext(dispatchers.io) {
        dataStore.edit { it[taxRateAsPercentKey] = asPercent }
    }

    suspend fun saveFontSize(fontSize: String) = withContext(dispatchers.io) {
        dataStore.edit { it[fontSizeKey] = fontSize }
    }

    suspend fun saveShoppingsDisplayTotal(displayTotal: String) = withContext(dispatchers.io) {
        dataStore.edit { it[shoppingsDisplayTotalKey] = displayTotal }
    }

    suspend fun saveProductsDisplayTotal(displayTotal: String) = withContext(dispatchers.io) {
        dataStore.edit { it[productsDisplayTotalKey] = displayTotal }
    }

    suspend fun saveProductsDisplayAutocomplete(displayAutocomplete: String) = withContext(dispatchers.io) {
        dataStore.edit { it[productsDisplayAutocompleteKey] = displayAutocomplete }
    }

    suspend fun saveProductsProductLock(productLock: String) = withContext(dispatchers.io) {
        dataStore.edit { it[productsProductLockKey] = productLock }
    }

    suspend fun saveDisplayCompleted(displayCompleted: String) = withContext(dispatchers.io) {
        dataStore.edit { it[displayCompletedKey] = displayCompleted }
    }

    suspend fun invertNightTheme() = withContext(dispatchers.io) {
        dataStore.edit {
            val nightTheme = it[nightThemeKey] ?: false
            it[nightThemeKey] = !nightTheme
        }
    }

    suspend fun invertCurrencyDisplayToLeft() = withContext(dispatchers.io) {
        dataStore.edit {
            val displayToLeft = it[currencyDisplayToLeftKey] ?: false
            it[currencyDisplayToLeftKey] = !displayToLeft
        }
    }

    suspend fun invertFirstLetterUppercase() = withContext(dispatchers.io) {
        dataStore.edit {
            val firstLetterUppercase = it[firstLetterUppercaseKey] ?: false
            it[firstLetterUppercaseKey] = !firstLetterUppercase
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

    suspend fun invertProductsEditCompleted() = withContext(dispatchers.io) {
        dataStore.edit {
            val editCompleted = it[productsEditCompletedKey] ?: false
            it[productsEditCompletedKey] = !editCompleted
        }
    }

    suspend fun invertProductsAddLastProduct() = withContext(dispatchers.io) {
        dataStore.edit {
            val addLastProduct = it[productsAddLastProductKey] ?: false
            it[productsAddLastProductKey] = !addLastProduct
        }
    }

    suspend fun invertProductsDisplayDefaultAutocomplete() = withContext(dispatchers.io) {
        dataStore.edit {
            val displayAutocomplete = it[productsDisplayDefaultAutocompleteKey] ?: true
            it[productsDisplayDefaultAutocompleteKey] = !displayAutocomplete
        }
    }

    suspend fun invertDisplayMoney() = withContext(dispatchers.io) {
        dataStore.edit {
            val displayMoney = it[displayMoneyKey] ?: false
            it[displayMoneyKey] = !displayMoney
        }
    }
}