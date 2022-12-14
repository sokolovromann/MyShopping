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
    private val shoppingsSortByKey = stringPreferencesKey("shoppings_sort_by")
    private val shoppingsSortAscendingKey = booleanPreferencesKey("shoppings_sort_ascending")
    private val shoppingsDisplayCompletedKey = stringPreferencesKey("shoppings_display_completed")
    private val shoppingsDisplayTotalKey = stringPreferencesKey("shoppings_display_total")
    private val productsMultiColumnsKey = booleanPreferencesKey("products_multi_columns")
    private val productsSortByKey = stringPreferencesKey("products_sort_by")
    private val productsSortAscendingKey = booleanPreferencesKey("products_sort_ascending")
    private val productsDisplayCompletedKey = stringPreferencesKey("products_display_completed")
    private val productsDisplayTotalKey = stringPreferencesKey("products_display_total")
    private val productsDisplayAutocompleteKey = stringPreferencesKey("products_display_autocomplete")
    private val productsLockQuantityKey = booleanPreferencesKey("products_lock_quantity")
    private val productsEditCompletedKey = booleanPreferencesKey("products_edit_completed")
    private val productsAddLastProductKey = booleanPreferencesKey("products_add_last_product")
    private val autocompletesSortByKey = stringPreferencesKey("autocompletes_sort_by")
    private val autocompletesSortAscendingKey = booleanPreferencesKey("autocompletes_sort_ascending")
    private val displayMoneyKey = booleanPreferencesKey("display_money")
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
                sortBy = it[shoppingsSortByKey] ?: "",
                sortAscending = it[shoppingsSortAscendingKey] ?: false,
                displayMoney = it[displayMoneyKey] ?: true,
                displayCompleted = it[shoppingsDisplayCompletedKey] ?: "",
                displayTotal = it[shoppingsDisplayTotalKey] ?: "",
                screenSize = it[screenSizeKey] ?: ""
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
                sortBy = it[productsSortByKey] ?: "",
                sortAscending = it[productsSortAscendingKey] ?: false,
                displayMoney = it[displayMoneyKey] ?: true,
                displayCompleted = it[productsDisplayCompletedKey] ?: "",
                displayTotal = it[productsDisplayTotalKey] ?: "",
                displayAutocomplete = it[productsDisplayAutocompleteKey] ?: "",
                lockQuantity = it[productsLockQuantityKey] ?: false,
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
                sortBy = it[autocompletesSortByKey] ?: "",
                sortAscending = it[autocompletesSortAscendingKey] ?: false,
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
                shoppingsMultiColumns = it[shoppingsMultiColumnsKey] ?: false,
                productsMultiColumns = it[productsMultiColumnsKey] ?: false,
                productsDisplayAutocomplete = it[productsDisplayAutocompleteKey] ?: "",
                productsEditCompleted = it[productsEditCompletedKey] ?: false,
                productsAddLastProduct = it[productsAddLastProductKey] ?: true
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

    suspend fun saveAppOpenedAction(appOpenedAction: String) = withContext(dispatchers.io) {
        dataStore.edit { it[appOpenedActionKey] = appOpenedAction }
    }

    suspend fun saveNightTheme(nightTheme: Boolean) = withContext(dispatchers.io) {
        dataStore.edit { it[nightThemeKey] = nightTheme }
    }

    suspend fun saveCurrency(currency: String) = withContext(dispatchers.io) {
        dataStore.edit { it[currencyKey] = currency }
    }

    suspend fun saveCurrencyDisplayToLeft(displayToLeft: Boolean) = withContext(dispatchers.io) {
        dataStore.edit { it[currencyDisplayToLeftKey] = displayToLeft }
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

    suspend fun saveFirstLetterUppercase(firstLetterUppercase: Boolean) = withContext(dispatchers.io) {
        dataStore.edit { it[firstLetterUppercaseKey] = firstLetterUppercase }
    }

    suspend fun saveShoppingsMultiColumns(multiColumns: Boolean) = withContext(dispatchers.io) {
        dataStore.edit { it[shoppingsMultiColumnsKey] = multiColumns }
    }

    suspend fun saveShoppingsSortBy(sortBy: String) = withContext(dispatchers.io) {
        dataStore.edit { it[shoppingsSortByKey] = sortBy }
    }

    suspend fun saveShoppingsSortAscending(ascending: Boolean) = withContext(dispatchers.io) {
        dataStore.edit { it[shoppingsSortAscendingKey] = ascending }
    }

    suspend fun saveShoppingsDisplayCompleted(displayCompleted: String) = withContext(dispatchers.io) {
        dataStore.edit { it[shoppingsDisplayCompletedKey] = displayCompleted }
    }

    suspend fun saveShoppingsDisplayTotal(displayTotal: String) = withContext(dispatchers.io) {
        dataStore.edit { it[shoppingsDisplayTotalKey] = displayTotal }
    }

    suspend fun saveProductsMultiColumns(multiColumns: Boolean) = withContext(dispatchers.io) {
        dataStore.edit { it[productsMultiColumnsKey] = multiColumns }
    }

    suspend fun saveProductsSortBy(sortBy: String) = withContext(dispatchers.io) {
        dataStore.edit { it[productsSortByKey] = sortBy }
    }

    suspend fun saveProductsSortAscending(ascending: Boolean) = withContext(dispatchers.io) {
        dataStore.edit { it[productsSortAscendingKey] = ascending }
    }

    suspend fun saveProductsDisplayCompleted(displayCompleted: String) = withContext(dispatchers.io) {
        dataStore.edit { it[productsDisplayCompletedKey] = displayCompleted }
    }

    suspend fun saveProductsDisplayTotal(displayTotal: String) = withContext(dispatchers.io) {
        dataStore.edit { it[productsDisplayTotalKey] = displayTotal }
    }

    suspend fun saveProductsDisplayAutocomplete(displayAutocomplete: String) = withContext(dispatchers.io) {
        dataStore.edit { it[productsDisplayAutocompleteKey] = displayAutocomplete }
    }

    suspend fun saveProductsLockQuantity(lockQuantity: Boolean) = withContext(dispatchers.io) {
        dataStore.edit { it[productsLockQuantityKey] = lockQuantity }
    }

    suspend fun saveProductsEditCompleted(editCompleted: Boolean) = withContext(dispatchers.io) {
        dataStore.edit { it[productsEditCompletedKey] = editCompleted }
    }

    suspend fun saveProductsAddLastProduct(addLastProduct: Boolean) = withContext(dispatchers.io) {
        dataStore.edit { it[productsAddLastProductKey] = addLastProduct }
    }

    suspend fun saveAutocompletesSortBy(sortBy: String) = withContext(dispatchers.io) {
        dataStore.edit { it[autocompletesSortByKey] = sortBy }
    }

    suspend fun saveAutocompletesSortAscending(ascending: Boolean) = withContext(dispatchers.io) {
        dataStore.edit { it[autocompletesSortAscendingKey] = ascending }
    }

    suspend fun saveDisplayMoney(displayMoney: Boolean) = withContext(dispatchers.io) {
        dataStore.edit { it[displayMoneyKey] = displayMoney }
    }

    suspend fun saveScreenSize(screenSize: String) = withContext(dispatchers.io) {
        dataStore.edit { it[screenSizeKey] = screenSize }
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

    suspend fun invertTaxRateAsPercent() = withContext(dispatchers.io) {
        dataStore.edit {
            val asPercent = it[taxRateAsPercentKey] ?: false
            it[taxRateAsPercentKey] = !asPercent
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

    suspend fun invertShoppingSortAscending() = withContext(dispatchers.io) {
        dataStore.edit {
            val ascending = it[shoppingsSortAscendingKey] ?: false
            it[shoppingsSortAscendingKey] = !ascending
        }
    }

    suspend fun invertProductsSortAscending() = withContext(dispatchers.io) {
        dataStore.edit {
            val ascending = it[productsSortAscendingKey] ?: false
            it[productsSortAscendingKey] = !ascending
        }
    }

    suspend fun invertProductsLockQuantity() = withContext(dispatchers.io) {
        dataStore.edit {
            val lockQuantity = it[productsLockQuantityKey] ?: false
            it[productsLockQuantityKey] = !lockQuantity
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

    suspend fun invertAutocompletesSortAscending() = withContext(dispatchers.io) {
        dataStore.edit {
            val ascending = it[autocompletesSortAscendingKey] ?: false
            it[autocompletesSortAscendingKey] = !ascending
        }
    }

    suspend fun invertDisplayMoney() = withContext(dispatchers.io) {
        dataStore.edit {
            val displayMoney = it[displayMoneyKey] ?: false
            it[displayMoneyKey] = !displayMoney
        }
    }
}