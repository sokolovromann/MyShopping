package ru.sokolovromann.myshopping.data.local.dao

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalAppConfigDatasource
import ru.sokolovromann.myshopping.data.local.entity.AppBuildConfigEntity
import ru.sokolovromann.myshopping.data.local.entity.AppConfigEntity
import ru.sokolovromann.myshopping.data.local.entity.DeviceConfigEntity
import ru.sokolovromann.myshopping.data.local.entity.UserPreferencesEntity
import javax.inject.Inject

class AppConfigDao @Inject constructor(
    private val datasource: LocalAppConfigDatasource,
    private val dispatchers: AppDispatchers
) {

    private val preferences = datasource.getPreferences()
    private val resources = datasource.getResources()

    suspend fun getAppConfig(): Flow<AppConfigEntity> = withContext(dispatchers.io) {
        return@withContext preferences.data.map { toAppConfigEntity(it) }
    }

    suspend fun saveAppConfig(appConfig: AppConfigEntity) = withContext(dispatchers.io) {
        saveBuildConfig(appConfig.appBuildConfig)
        saveUserPreferences(appConfig.userPreferences)
    }

    suspend fun saveFontSize(value: String) = withContext(dispatchers.io) {
        preferences.edit {
            it[DatasourceKey.User.fontSize] = value
        }
    }

    suspend fun saveCurrency(value: String) = withContext(dispatchers.io) {
        preferences.edit {
            it[DatasourceKey.User.currency] = value
        }
    }

    suspend fun saveTaxRate(value: Float) = withContext(dispatchers.io) {
        preferences.edit {
            it[DatasourceKey.User.taxRate] = value
        }
    }

    suspend fun saveTaxRateAsPercent(value: Boolean) = withContext(dispatchers.io) {
        preferences.edit {
            it[DatasourceKey.User.taxRateAsPercent] = value
        }
    }

    suspend fun savePurchasesSeparator(value: String) = withContext(dispatchers.io) {
        preferences.edit {
            it[DatasourceKey.User.purchasesSeparator] = value
        }
    }

    suspend fun saveMaxAutocompleteNames(value: Int) = withContext(dispatchers.io) {
        preferences.edit {
            it[DatasourceKey.User.maxAutocompletesNames] = value
        }
    }

    suspend fun saveMaxAutocompleteQuantities(value: Int) = withContext(dispatchers.io) {
        preferences.edit {
            it[DatasourceKey.User.maxAutocompletesQuantities] = value
        }
    }

    suspend fun saveMaxAutocompleteMoneys(value: Int) = withContext(dispatchers.io) {
        preferences.edit {
            it[DatasourceKey.User.maxAutocompletesMoneys] = value
        }
    }

    suspend fun saveMaxAutocompleteOthers(value: Int) = withContext(dispatchers.io) {
        preferences.edit {
            it[DatasourceKey.User.maxAutocompletesOthers] = value
        }
    }

    suspend fun saveMinMoneyFractionDigits(value: Int) = withContext(dispatchers.io) {
        preferences.edit {
            it[DatasourceKey.User.minMoneyFractionDigits] = value
        }
    }

    suspend fun saveMinQuantityFractionDigits(value: Int) = withContext(dispatchers.io) {
        preferences.edit {
            it[DatasourceKey.User.minQuantityFractionDigits] = value
        }
    }

    suspend fun saveMaxMoneyFractionDigits(value: Int) = withContext(dispatchers.io) {
        preferences.edit {
            it[DatasourceKey.User.maxMoneyFractionDigits] = value
        }
    }

    suspend fun saveMaxQuantityFractionDigits(value: Int) = withContext(dispatchers.io) {
        preferences.edit {
            it[DatasourceKey.User.maxQuantityFractionDigits] = value
        }
    }

    suspend fun displayCompleted(value: String) = withContext(dispatchers.io) {
        preferences.edit {
            it[DatasourceKey.User.displayCompleted] = value
        }
    }

    suspend fun displayTotal(value: String) = withContext(dispatchers.io) {
        preferences.edit {
            it[DatasourceKey.User.displayTotal] = value
        }
    }

    suspend fun displayShoppingsProducts(value: String) = withContext(dispatchers.io) {
        preferences.edit {
            it[DatasourceKey.User.displayShoppingsProducts] = value
        }
    }

    suspend fun lockProductElement(value: String) = withContext(dispatchers.io) {
        preferences.edit {
            it[DatasourceKey.User.lockProductElement] = value
        }
    }

    suspend fun invertNightTheme() = withContext(dispatchers.io) {
        preferences.edit {
            val value = it[DatasourceKey.User.nightTheme]
            it[DatasourceKey.User.nightTheme] = !requireNotNull(value)
        }
    }

    suspend fun invertDisplayMoney() = withContext(dispatchers.io) {
        preferences.edit {
            val value = it[DatasourceKey.User.displayMoney]
            it[DatasourceKey.User.displayMoney] = !requireNotNull(value)
        }
    }

    suspend fun invertDisplayCurrencyToLeft() = withContext(dispatchers.io) {
        preferences.edit {
            val value = it[DatasourceKey.User.displayCurrencyToLeft]
            it[DatasourceKey.User.displayCurrencyToLeft] = !requireNotNull(value)
        }
    }

    suspend fun invertTaxRateAsPercent() = withContext(dispatchers.io) {
        preferences.edit {
            val value = it[DatasourceKey.User.taxRateAsPercent]
            it[DatasourceKey.User.taxRateAsPercent] = !requireNotNull(value)
        }
    }

    suspend fun invertShoppingsMultiColumns() = withContext(dispatchers.io) {
        preferences.edit {
            val value = it[DatasourceKey.User.shoppingsMultiColumns]
            it[DatasourceKey.User.shoppingsMultiColumns] = !requireNotNull(value)
        }
    }

    suspend fun invertProductsMultiColumns() = withContext(dispatchers.io) {
        preferences.edit {
            val value = it[DatasourceKey.User.productsMultiColumns]
            it[DatasourceKey.User.productsMultiColumns] = !requireNotNull(value)
        }
    }

    suspend fun invertDisplayOtherFields() = withContext(dispatchers.io) {
        preferences.edit {
            val value = it[DatasourceKey.User.displayOtherFields]
            it[DatasourceKey.User.displayOtherFields] = !requireNotNull(value)
        }
    }

    suspend fun invertColoredCheckbox() = withContext(dispatchers.io) {
        preferences.edit {
            val value = it[DatasourceKey.User.coloredCheckbox]
            it[DatasourceKey.User.coloredCheckbox] = !requireNotNull(value)
        }
    }

    suspend fun invertEditProductAfterCompleted() = withContext(dispatchers.io) {
        preferences.edit {
            val value = it[DatasourceKey.User.editProductAfterCompleted]
            it[DatasourceKey.User.editProductAfterCompleted] = !requireNotNull(value)
        }
    }

    suspend fun invertCompletedWithCheckbox() = withContext(dispatchers.io) {
        preferences.edit {
            val value = it[DatasourceKey.User.completedWithCheckbox]
            it[DatasourceKey.User.completedWithCheckbox] = !requireNotNull(value)
        }
    }

    suspend fun invertEnterToSaveProduct() = withContext(dispatchers.io) {
        preferences.edit {
            val value = it[DatasourceKey.User.enterToSaveProduct]
            it[DatasourceKey.User.enterToSaveProduct] = !requireNotNull(value)
        }
    }

    suspend fun invertDisplayDefaultAutocompletes() = withContext(dispatchers.io) {
        preferences.edit {
            val value = it[DatasourceKey.User.displayDefaultAutocompletes]
            it[DatasourceKey.User.displayDefaultAutocompletes] = !requireNotNull(value)
        }
    }

    suspend fun invertSaveProductToAutocompletes() = withContext(dispatchers.io) {
        preferences.edit {
            val value = it[DatasourceKey.User.saveProductToAutocompletes]
            it[DatasourceKey.User.saveProductToAutocompletes] = !requireNotNull(value)
        }
    }

    private suspend fun saveBuildConfig(entity: AppBuildConfigEntity) {
        preferences.edit {
            it[DatasourceKey.Build.appFirstTime] = requireNotNull(entity.appFirstTime)
            it[DatasourceKey.Build.userCodeVersion] = requireNotNull(entity.userCodeVersion)
        }
    }

    private suspend fun saveUserPreferences(entity: UserPreferencesEntity) {
        preferences.edit {
            it[DatasourceKey.User.nightTheme] = requireNotNull(entity.nightTheme)
            it[DatasourceKey.User.fontSize] = requireNotNull(entity.fontSize)
            it[DatasourceKey.User.shoppingsMultiColumns] = requireNotNull(entity.shoppingsMultiColumns)
            it[DatasourceKey.User.productsMultiColumns] = requireNotNull(entity.productsMultiColumns)
            it[DatasourceKey.User.displayCompleted] = requireNotNull(entity.displayCompleted)
            it[DatasourceKey.User.displayTotal] = requireNotNull(entity.displayTotal)
            it[DatasourceKey.User.displayOtherFields] = requireNotNull(entity.displayOtherFields)
            it[DatasourceKey.User.coloredCheckbox] = requireNotNull(entity.coloredCheckbox)
            it[DatasourceKey.User.displayShoppingsProducts] = requireNotNull(entity.displayShoppingsProducts)
            it[DatasourceKey.User.purchasesSeparator] = requireNotNull(entity.purchasesSeparator)
            it[DatasourceKey.User.editProductAfterCompleted] = requireNotNull(entity.editProductAfterCompleted)
            it[DatasourceKey.User.lockProductElement] = requireNotNull(entity.lockProductElement)
            it[DatasourceKey.User.completedWithCheckbox] = requireNotNull(entity.completedWithCheckbox)
            it[DatasourceKey.User.enterToSaveProduct] = requireNotNull(entity.enterToSaveProduct)
            it[DatasourceKey.User.displayDefaultAutocompletes] = requireNotNull(entity.displayDefaultAutocompletes)
            it[DatasourceKey.User.maxAutocompletesNames] = requireNotNull(entity.maxAutocompletesNames)
            it[DatasourceKey.User.maxAutocompletesQuantities] = requireNotNull(entity.maxAutocompletesQuantities)
            it[DatasourceKey.User.maxAutocompletesMoneys] = requireNotNull(entity.maxAutocompletesMoneys)
            it[DatasourceKey.User.maxAutocompletesOthers] = requireNotNull(entity.maxAutocompletesOthers)
            it[DatasourceKey.User.saveProductToAutocompletes] = requireNotNull(entity.saveProductToAutocompletes)
            it[DatasourceKey.User.displayMoney] = requireNotNull(entity.displayMoney)
            it[DatasourceKey.User.currency] = requireNotNull(entity.currency)
            it[DatasourceKey.User.displayCurrencyToLeft] = requireNotNull(entity.displayCurrencyToLeft)
            it[DatasourceKey.User.taxRate] = requireNotNull(entity.taxRate)
            it[DatasourceKey.User.taxRateAsPercent] = requireNotNull(entity.taxRateAsPercent)
            it[DatasourceKey.User.minMoneyFractionDigits] = requireNotNull(entity.minMoneyFractionDigits)
            it[DatasourceKey.User.minQuantityFractionDigits] = requireNotNull(entity.minQuantityFractionDigits)
            it[DatasourceKey.User.maxMoneyFractionDigits] = requireNotNull(entity.maxMoneyFractionDigits)
            it[DatasourceKey.User.maxQuantityFractionDigits] = requireNotNull(entity.maxQuantityFractionDigits)
        }
    }

    private fun toAppConfigEntity(preferences: Preferences): AppConfigEntity {
        return AppConfigEntity(
            deviceConfig = getDeviceConfig(),
            appBuildConfig = toAppBuildConfig(preferences),
            userPreferences = toUserPreferences(preferences)
        )
    }

    private fun getDeviceConfig(): DeviceConfigEntity {
        return DeviceConfigEntity(
            screenWidthDp = resources.configuration.screenWidthDp,
            screenHeightDp = resources.configuration.screenHeightDp
        )
    }

    private fun toAppBuildConfig(preferences: Preferences): AppBuildConfigEntity {
        return AppBuildConfigEntity(
            appFirstTime = preferences[DatasourceKey.Build.appFirstTime],
            userCodeVersion = preferences[DatasourceKey.Build.userCodeVersion]
        )
    }

    private fun toUserPreferences(preferences: Preferences): UserPreferencesEntity {
        return UserPreferencesEntity(
            nightTheme = preferences[DatasourceKey.User.nightTheme],
            fontSize = preferences[DatasourceKey.User.fontSize],
            shoppingsMultiColumns = preferences[DatasourceKey.User.shoppingsMultiColumns],
            productsMultiColumns = preferences[DatasourceKey.User.productsMultiColumns],
            displayCompleted = preferences[DatasourceKey.User.displayCompleted],
            displayTotal = preferences[DatasourceKey.User.displayTotal],
            displayOtherFields = preferences[DatasourceKey.User.displayOtherFields],
            coloredCheckbox = preferences[DatasourceKey.User.coloredCheckbox],
            displayShoppingsProducts = preferences[DatasourceKey.User.displayShoppingsProducts],
            purchasesSeparator = preferences[DatasourceKey.User.purchasesSeparator],
            editProductAfterCompleted = preferences[DatasourceKey.User.editProductAfterCompleted],
            lockProductElement = preferences[DatasourceKey.User.lockProductElement],
            completedWithCheckbox = preferences[DatasourceKey.User.completedWithCheckbox],
            enterToSaveProduct = preferences[DatasourceKey.User.enterToSaveProduct],
            displayDefaultAutocompletes = preferences[DatasourceKey.User.displayDefaultAutocompletes],
            maxAutocompletesNames = preferences[DatasourceKey.User.maxAutocompletesNames],
            maxAutocompletesQuantities = preferences[DatasourceKey.User.maxAutocompletesQuantities],
            maxAutocompletesMoneys = preferences[DatasourceKey.User.maxAutocompletesMoneys],
            maxAutocompletesOthers = preferences[DatasourceKey.User.maxAutocompletesOthers],
            saveProductToAutocompletes = preferences[DatasourceKey.User.saveProductToAutocompletes],
            displayMoney = preferences[DatasourceKey.User.displayMoney],
            currency = preferences[DatasourceKey.User.currency],
            displayCurrencyToLeft = preferences[DatasourceKey.User.displayCurrencyToLeft],
            taxRate = preferences[DatasourceKey.User.taxRate],
            taxRateAsPercent = preferences[DatasourceKey.User.taxRateAsPercent],
            minMoneyFractionDigits = preferences[DatasourceKey.User.minMoneyFractionDigits],
            minQuantityFractionDigits = preferences[DatasourceKey.User.minQuantityFractionDigits],
            maxMoneyFractionDigits = preferences[DatasourceKey.User.maxMoneyFractionDigits],
            maxQuantityFractionDigits = preferences[DatasourceKey.User.maxQuantityFractionDigits]
        )
    }
}

private object DatasourceKey {
    object Build {
        val appFirstTime = stringPreferencesKey("app_first_time")
        val userCodeVersion = intPreferencesKey("user_code_version")
    }

    object User {
        val nightTheme = booleanPreferencesKey("night_theme")
        val fontSize = stringPreferencesKey("font_size")
        val displayMoney = booleanPreferencesKey("display_money")
        val currency = stringPreferencesKey("currency")
        val displayCurrencyToLeft= booleanPreferencesKey("display_currency_to_left")
        val taxRate = floatPreferencesKey("tax_rate")
        val taxRateAsPercent = booleanPreferencesKey("tax_rate_as_percent")
        val shoppingsMultiColumns = booleanPreferencesKey("shoppings_multi_columns")
        val productsMultiColumns = booleanPreferencesKey("products_multi_columns")
        val displayCompleted = stringPreferencesKey("display_completed_purchases")
        val displayTotal = stringPreferencesKey("display_purchases_total")
        val displayOtherFields = booleanPreferencesKey("display_purchases_other_fields")
        val coloredCheckbox = booleanPreferencesKey("highlight_checkbox")
        val displayShoppingsProducts = stringPreferencesKey("display_shoppings_products")
        val purchasesSeparator = stringPreferencesKey("purchases_separator")
        val editProductAfterCompleted = booleanPreferencesKey("edit_product_after_completed")
        val lockProductElement = stringPreferencesKey("lock_product_element")
        val completedWithCheckbox = booleanPreferencesKey("completed_with_checkbox")
        val enterToSaveProduct = booleanPreferencesKey("enter_to_save_product")
        val displayDefaultAutocompletes = booleanPreferencesKey("display_default_autocompletes")
        val maxAutocompletesNames = intPreferencesKey("max_autocomplete_names")
        val maxAutocompletesQuantities = intPreferencesKey("max_autocomplete_quantities")
        val maxAutocompletesMoneys = intPreferencesKey("max_autocomplete_moneys")
        val maxAutocompletesOthers = intPreferencesKey("max_autocomplete_others")
        val saveProductToAutocompletes = booleanPreferencesKey("save_product_to_autocompletes")
        val minMoneyFractionDigits = intPreferencesKey("min_money_fraction_digits")
        val minQuantityFractionDigits = intPreferencesKey("min_quantity_fraction_digits")
        val maxMoneyFractionDigits = intPreferencesKey("max_money_fraction_digits")
        val maxQuantityFractionDigits = intPreferencesKey("max_quantity_fraction_digits")
    }
}