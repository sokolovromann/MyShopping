package ru.sokolovromann.myshopping.data.local.dao

import android.content.SharedPreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.AppContent
import ru.sokolovromann.myshopping.data.local.entity.AppBuildConfigEntity
import ru.sokolovromann.myshopping.data.local.entity.AppConfigEntity
import ru.sokolovromann.myshopping.data.local.entity.DeviceConfigEntity
import ru.sokolovromann.myshopping.data.local.entity.UserPreferencesEntity
import ru.sokolovromann.myshopping.data.local.entity.CodeVersion14UserPreferencesEntity

class AppConfigDao(appContent: AppContent) {

    private val preferences = appContent.getPreferences()
    private val userSharedPreferences = appContent.getUserSharedPreferences()
    private val openedSharedPreferences = appContent.getOpenedSharedPreferences()
    private val resources = appContent.getResources()

    suspend fun getAppConfig(): Flow<AppConfigEntity> = withContext(AppDispatchers.IO) {
        return@withContext preferences.data.map { toAppConfigEntity(it) }
    }

    suspend fun getCodeVersion14Preferences(): Flow<CodeVersion14UserPreferencesEntity> = withContext(AppDispatchers.IO) {
        return@withContext preferences.data.map { toVer14UserPreferences() }
    }

    suspend fun saveAppConfig(appConfig: AppConfigEntity) = withContext(AppDispatchers.IO) {
        saveBuildConfig(appConfig.appBuildConfig)
        saveUserPreferences(appConfig.userPreferences)
    }

    suspend fun saveUserCodeVersion(userCodeVersion: Int) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.Build.userCodeVersion] = userCodeVersion
        }
    }

    suspend fun saveNightTheme(
        appNightTheme: Boolean,
        widgetNightTheme: Boolean
    ) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.appNightTheme] = appNightTheme
            it[DatasourceKey.User.widgetNightTheme] = widgetNightTheme
        }
    }

    suspend fun saveFontSize(
        appFontSize: String,
        widgetFontSize: String
    ) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.appFontSize] = appFontSize
            it[DatasourceKey.User.widgetFontSize] = widgetFontSize
        }
    }

    suspend fun saveCurrency(value: String) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.currency] = value
        }
    }

    suspend fun saveTaxRate(value: Float) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.taxRate] = value
        }
    }

    suspend fun saveMaxAutocompleteNames(value: Int) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.maxAutocompletesNames] = value
        }
    }

    suspend fun saveMaxAutocompleteQuantities(value: Int) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.maxAutocompletesQuantities] = value
        }
    }

    suspend fun saveMaxAutocompleteMoneys(value: Int) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.maxAutocompletesMoneys] = value
        }
    }

    suspend fun saveMaxAutocompleteOthers(value: Int) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.maxAutocompletesOthers] = value
        }
    }

    suspend fun saveMinMoneyFractionDigits(value: Int) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.minMoneyFractionDigits] = value
        }
    }

    suspend fun saveAfterSaveProduct(value: String) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.afterSaveProduct] = value
        }
    }

    suspend fun saveAfterProductCompleted(value: String, editAfterCompleted: Boolean) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.afterProductCompleted] = value
            it[DatasourceKey.User.editProductAfterCompleted] = editAfterCompleted
        }
    }

    suspend fun saveAfterAddShopping(value: String) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.afterAddShopping] = value
        }
    }

    suspend fun saveAfterShoppingCompleted(value: String) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.afterShoppingCompleted] = value
        }
    }

    suspend fun saveSwipeProductLeft(value: String) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.swipeProductLeft] = value
        }
    }

    suspend fun saveSwipeProductRight(value: String) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.swipeProductRight] = value
        }
    }

    suspend fun saveSwipeShoppingLeft(value: String) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.swipeShoppingLeft] = value
        }
    }

    suspend fun saveSwipeShoppingRight(value: String) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.swipeShoppingRight] = value
        }
    }

    suspend fun enableAutomaticShoppingsSort(
        sortBy: String,
        ascending: Boolean
    ) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.shoppingsSortBy] = sortBy
            it[DatasourceKey.User.shoppingsSortAscending] = ascending
            it[DatasourceKey.User.shoppingsSortFormatted] = true
        }
    }

    suspend fun disableAutomaticShoppingsSort(
        sortBy: String,
        ascending: Boolean
    ) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.shoppingsSortBy] = sortBy
            it[DatasourceKey.User.shoppingsSortAscending] = ascending
            it[DatasourceKey.User.shoppingsSortFormatted] = false
        }
    }

    suspend fun displayCompleted(
        appDisplayCompleted: String,
        widgetDisplayCompleted: String
    ) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.displayCompleted] = appDisplayCompleted
            it[DatasourceKey.User.widgetDisplayCompleted] = widgetDisplayCompleted
        }
    }

    suspend fun displayTotal(value: String) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.displayTotal] = value
        }
    }

    suspend fun displayShoppingsProducts(value: String) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.displayShoppingsProducts] = value
        }
    }

    suspend fun lockProductElement(value: String) = withContext(AppDispatchers.IO) {
        preferences.edit {
            it[DatasourceKey.User.lockProductElement] = value
        }
    }

    suspend fun invertLongTotal(valueIfNull: Boolean) = withContext(AppDispatchers.IO) {
        preferences.edit {
            val oldValue = it[DatasourceKey.User.displayLongTotal]
            val newValue = if (oldValue == null) valueIfNull else !oldValue
            it[DatasourceKey.User.displayLongTotal] = newValue
        }
    }

    suspend fun invertDisplayMoney(valueIfNull: Boolean) = withContext(AppDispatchers.IO) {
        preferences.edit {
            val oldValue = it[DatasourceKey.User.displayMoney]
            val newValue = if (oldValue == null) valueIfNull else !oldValue
            it[DatasourceKey.User.displayMoney] = newValue
        }
    }

    suspend fun invertDisplayCurrencyToLeft(valueIfNull: Boolean) = withContext(AppDispatchers.IO) {
        preferences.edit {
            val oldValue = it[DatasourceKey.User.displayCurrencyToLeft]
            val newValue = if (oldValue == null) valueIfNull else !oldValue
            it[DatasourceKey.User.displayCurrencyToLeft] = newValue
        }
    }

    suspend fun invertShoppingsMultiColumns(valueIfNull: Boolean) = withContext(AppDispatchers.IO) {
        preferences.edit {
            val oldValue = it[DatasourceKey.User.shoppingsMultiColumns]
            val newValue = if (oldValue == null) valueIfNull else !oldValue
            it[DatasourceKey.User.shoppingsMultiColumns] = newValue
        }
    }

    suspend fun invertProductsMultiColumns(valueIfNull: Boolean) = withContext(AppDispatchers.IO) {
        preferences.edit {
            val oldValue = it[DatasourceKey.User.productsMultiColumns]
            val newValue = if (oldValue == null) valueIfNull else !oldValue
            it[DatasourceKey.User.productsMultiColumns] = newValue
        }
    }

    suspend fun invertStrikethroughCompletedProducts(valueIfNull: Boolean) = withContext(AppDispatchers.IO) {
        preferences.edit {
            val oldValue = it[DatasourceKey.User.strikethroughCompletedProducts]
            val newValue = if (oldValue == null) valueIfNull else !oldValue
            it[DatasourceKey.User.strikethroughCompletedProducts] = newValue
        }
    }

    suspend fun invertDisplayOtherFields(valueIfNull: Boolean) = withContext(AppDispatchers.IO) {
        preferences.edit {
            val oldValue = it[DatasourceKey.User.displayOtherFields]
            val newValue = if (oldValue == null) valueIfNull else !oldValue
            it[DatasourceKey.User.displayOtherFields] = newValue
        }
    }

    suspend fun invertColoredCheckbox(valueIfNull: Boolean) = withContext(AppDispatchers.IO) {
        preferences.edit {
            val oldValue = it[DatasourceKey.User.coloredCheckbox]
            val newValue = if (oldValue == null) valueIfNull else !oldValue
            it[DatasourceKey.User.coloredCheckbox] = newValue
        }
    }

    suspend fun invertCompletedWithCheckbox(valueIfNull: Boolean) = withContext(AppDispatchers.IO) {
        preferences.edit {
            val oldValue = it[DatasourceKey.User.completedWithCheckbox]
            val newValue = if (oldValue == null) valueIfNull else !oldValue
            it[DatasourceKey.User.completedWithCheckbox] = newValue
        }
    }

    suspend fun invertEnterToSaveProduct(valueIfNull: Boolean) = withContext(AppDispatchers.IO) {
        preferences.edit {
            val oldValue = it[DatasourceKey.User.enterToSaveProduct]
            val newValue = if (oldValue == null) valueIfNull else !oldValue
            it[DatasourceKey.User.enterToSaveProduct] = newValue
        }
    }

    suspend fun invertDisplayDefaultAutocompletes(valueIfNull: Boolean) = withContext(AppDispatchers.IO) {
        preferences.edit {
            val oldValue = it[DatasourceKey.User.displayDefaultAutocompletes]
            val newValue = if (oldValue == null) valueIfNull else !oldValue
            it[DatasourceKey.User.displayDefaultAutocompletes] = newValue
        }
    }

    suspend fun invertSaveProductToAutocompletes(valueIfNull: Boolean) = withContext(AppDispatchers.IO) {
        preferences.edit {
            val oldValue = it[DatasourceKey.User.saveProductToAutocompletes]
            val newValue = if (oldValue == null) valueIfNull else !oldValue
            it[DatasourceKey.User.saveProductToAutocompletes] = newValue
        }
    }

    suspend fun invertAutomaticallyEmptyTrash(valueIfNull: Boolean) = withContext(AppDispatchers.IO) {
        preferences.edit {
            val oldValue = it[DatasourceKey.User.automaticallyEmptyTrash]
            val newValue = if (oldValue == null) valueIfNull else !oldValue
            it[DatasourceKey.User.automaticallyEmptyTrash] = newValue
        }
    }

    suspend fun invertDisplayListOfAutocompletes(valueIfNull: Boolean) = withContext(AppDispatchers.IO) {
        preferences.edit {
            val oldValue = it[DatasourceKey.User.displayListOfAutocompletes]
            val newValue = if (oldValue == null) valueIfNull else !oldValue
            it[DatasourceKey.User.displayListOfAutocompletes] = newValue
        }
    }

    suspend fun invertDisplayEmptyShoppings(valueIfNull: Boolean) = withContext(AppDispatchers.IO) {
        preferences.edit {
            val oldValue = it[DatasourceKey.User.displayEmptyShoppings]
            val newValue = if (oldValue == null) valueIfNull else !oldValue
            it[DatasourceKey.User.displayEmptyShoppings] = newValue
        }
    }

    suspend fun invertArchiveAsCompleted(valueIfNull: Boolean) = withContext(AppDispatchers.IO) {
        preferences.edit {
            val oldValue = it[DatasourceKey.User.archiveAsCompleted]
            val newValue = if (oldValue == null) valueIfNull else !oldValue
            it[DatasourceKey.User.archiveAsCompleted] = newValue
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
            it[DatasourceKey.User.appNightTheme] = entity.nightTheme ?: false
            it[DatasourceKey.User.widgetNightTheme] = entity.widgetNightTheme ?: false
            it[DatasourceKey.User.appFontSize] = entity.fontSize ?: ""
            it[DatasourceKey.User.widgetFontSize] = entity.widgetFontSize ?: (entity.fontSize ?: "")
            it[DatasourceKey.User.shoppingsMultiColumns] = requireNotNull(entity.shoppingsMultiColumns)
            it[DatasourceKey.User.shoppingsSortBy] = entity.shoppingsSortBy ?: ""
            it[DatasourceKey.User.shoppingsSortAscending] = entity.shoppingsSortAscending ?: true
            it[DatasourceKey.User.shoppingsSortFormatted] = entity.shoppingsSortFormatted ?: false
            it[DatasourceKey.User.productsMultiColumns] = requireNotNull(entity.productsMultiColumns)
            it[DatasourceKey.User.displayCompleted] = entity.displayCompleted ?: ""
            it[DatasourceKey.User.widgetDisplayCompleted] = entity.widgetDisplayCompleted ?: (entity.displayCompleted ?: "")
            it[DatasourceKey.User.strikethroughCompletedProducts] = entity.strikethroughCompletedProducts ?: false
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
            it[DatasourceKey.User.automaticallyEmptyTrash] = entity.automaticallyEmptyTrash ?: false
            it[DatasourceKey.User.displayListOfAutocompletes] = entity.displayListOfAutocompletes ?: false
            it[DatasourceKey.User.afterSaveProduct] = entity.afterSaveProduct ?: ""
            it[DatasourceKey.User.afterProductCompleted] = entity.afterProductCompleted ?: ""
            it[DatasourceKey.User.afterAddShopping] = entity.afterAddShopping ?: ""
            it[DatasourceKey.User.afterShoppingCompleted] = entity.afterShoppingCompleted ?: ""
            it[DatasourceKey.User.displayEmptyShoppings] = entity.displayEmptyShoppings ?: true
            it[DatasourceKey.User.swipeProductLeft] = entity.swipeProductLeft ?: ""
            it[DatasourceKey.User.swipeProductRight] = entity.swipeProductRight ?: ""
            it[DatasourceKey.User.swipeShoppingLeft] = entity.swipeShoppingLeft ?: ""
            it[DatasourceKey.User.swipeShoppingRight] = entity.swipeShoppingRight ?: ""
            it[DatasourceKey.User.archiveAsCompleted] = entity.archiveAsCompleted ?: false
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
        val codeVersion14 = openedSharedPreferences.contains(DatasourceKey.CodeVersion14.firstOpened)

        return AppBuildConfigEntity(
            appFirstTime = preferences[DatasourceKey.Build.appFirstTime],
            codeVersion14 = codeVersion14,
            userCodeVersion = preferences[DatasourceKey.Build.userCodeVersion]
        )
    }

    private fun toUserPreferences(preferences: Preferences): UserPreferencesEntity {
        return UserPreferencesEntity(
            nightTheme = preferences[DatasourceKey.User.appNightTheme],
            widgetNightTheme = preferences[DatasourceKey.User.widgetNightTheme],
            fontSize = preferences[DatasourceKey.User.appFontSize],
            widgetFontSize = preferences[DatasourceKey.User.widgetFontSize],
            shoppingsMultiColumns = preferences[DatasourceKey.User.shoppingsMultiColumns],
            shoppingsSortBy = preferences[DatasourceKey.User.shoppingsSortBy],
            shoppingsSortAscending = preferences[DatasourceKey.User.shoppingsSortAscending],
            shoppingsSortFormatted = preferences[DatasourceKey.User.shoppingsSortFormatted],
            productsMultiColumns = preferences[DatasourceKey.User.productsMultiColumns],
            displayCompleted = preferences[DatasourceKey.User.displayCompleted],
            widgetDisplayCompleted = preferences[DatasourceKey.User.widgetDisplayCompleted],
            strikethroughCompletedProducts = preferences[DatasourceKey.User.strikethroughCompletedProducts],
            displayTotal = preferences[DatasourceKey.User.displayTotal],
            displayLongTotal = preferences[DatasourceKey.User.displayLongTotal],
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
            maxQuantityFractionDigits = preferences[DatasourceKey.User.maxQuantityFractionDigits],
            automaticallyEmptyTrash = preferences[DatasourceKey.User.automaticallyEmptyTrash],
            displayListOfAutocompletes = preferences[DatasourceKey.User.displayListOfAutocompletes],
            afterSaveProduct = preferences[DatasourceKey.User.afterSaveProduct],
            afterProductCompleted = preferences[DatasourceKey.User.afterProductCompleted],
            afterAddShopping = preferences[DatasourceKey.User.afterAddShopping],
            afterShoppingCompleted = preferences[DatasourceKey.User.afterShoppingCompleted],
            displayEmptyShoppings = preferences[DatasourceKey.User.displayEmptyShoppings],
            swipeProductLeft = preferences[DatasourceKey.User.swipeProductLeft],
            swipeProductRight = preferences[DatasourceKey.User.swipeProductRight],
            swipeShoppingLeft = preferences[DatasourceKey.User.swipeShoppingLeft],
            swipeShoppingRight = preferences[DatasourceKey.User.swipeShoppingRight],
            archiveAsCompleted = preferences[DatasourceKey.User.archiveAsCompleted]
        )
    }

    private fun toVer14UserPreferences(): CodeVersion14UserPreferencesEntity {
        return CodeVersion14UserPreferencesEntity(
            firstOpened = openedSharedPreferences.getBooleanOrNull(DatasourceKey.CodeVersion14.firstOpened),
            currency = userSharedPreferences.getStringOrNull(DatasourceKey.CodeVersion14.currency),
            displayCurrencyToLeft = userSharedPreferences.getBooleanOrNull(DatasourceKey.CodeVersion14.displayCurrencyToLeft),
            taxRate = userSharedPreferences.getFloatOrNull(DatasourceKey.CodeVersion14.taxRate),
            titleFontSize = userSharedPreferences.getIntOrNull(DatasourceKey.CodeVersion14.titleFontSize),
            bodyFontSize = userSharedPreferences.getIntOrNull(DatasourceKey.CodeVersion14.bodyFontSize),
            firstLetterUppercase = userSharedPreferences.getBooleanOrNull(DatasourceKey.CodeVersion14.firstLetterUppercase),
            columnCount = userSharedPreferences.getIntOrNull(DatasourceKey.CodeVersion14.columnCount),
            sort = userSharedPreferences.getIntOrNull(DatasourceKey.CodeVersion14.sort),
            displayMoney = userSharedPreferences.getBooleanOrNull(DatasourceKey.CodeVersion14.displayMoney),
            displayTotal = userSharedPreferences.getIntOrNull(DatasourceKey.CodeVersion14.displayTotal),
            editProductAfterCompleted = userSharedPreferences.getBooleanOrNull(DatasourceKey.CodeVersion14.editProductAfterCompleted),
            saveProductToAutocompletes = userSharedPreferences.getBooleanOrNull(DatasourceKey.CodeVersion14.saveProductToAutocompletes)
        )
    }

    private fun SharedPreferences.getStringOrNull(key: String): String? {
        return if (contains(key)) getString(key, "") else null
    }

    private fun SharedPreferences.getIntOrNull(key: String): Int? {
        return if (contains(key)) getInt(key, Int.MIN_VALUE) else null
    }

    private fun SharedPreferences.getFloatOrNull(key: String): Float? {
        return if (contains(key)) getFloat(key, Float.MIN_VALUE) else null
    }

    private fun SharedPreferences.getBooleanOrNull(key: String): Boolean? {
        return if (contains(key)) getBoolean(key, false) else null
    }
}

private object DatasourceKey {
    object Build {
        val appFirstTime = stringPreferencesKey("app_first_time")
        val userCodeVersion = intPreferencesKey("user_code_version")
    }

    object User {
        val appNightTheme = booleanPreferencesKey("night_theme")
        val widgetNightTheme = booleanPreferencesKey("widget_night_theme")
        val appFontSize = stringPreferencesKey("font_size")
        val widgetFontSize = stringPreferencesKey("widget_font_size")
        val displayMoney = booleanPreferencesKey("display_money")
        val currency = stringPreferencesKey("currency")
        val displayCurrencyToLeft= booleanPreferencesKey("display_currency_to_left")
        val taxRate = floatPreferencesKey("tax_rate")
        val taxRateAsPercent = booleanPreferencesKey("tax_rate_as_percent")
        val shoppingsMultiColumns = booleanPreferencesKey("shoppings_multi_columns")
        val shoppingsSortBy = stringPreferencesKey("shoppings_sort_by")
        val shoppingsSortAscending = booleanPreferencesKey("shoppings_sort_ascending")
        val shoppingsSortFormatted = booleanPreferencesKey("shoppings_sort_formatted")
        val productsMultiColumns = booleanPreferencesKey("products_multi_columns")
        val displayCompleted = stringPreferencesKey("display_completed_purchases")
        val widgetDisplayCompleted = stringPreferencesKey("widget_display_completed_purchases")
        val strikethroughCompletedProducts = booleanPreferencesKey("strikethrough_completed_products")
        val displayTotal = stringPreferencesKey("display_purchases_total")
        val displayLongTotal = booleanPreferencesKey("display_long_purchases_total")
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
        val automaticallyEmptyTrash = booleanPreferencesKey("automatically_empty_trash")
        val displayListOfAutocompletes = booleanPreferencesKey("display_list_of_autocompletes")
        val afterSaveProduct = stringPreferencesKey("after_save_product")
        val afterProductCompleted = stringPreferencesKey("after_product_completed")
        val afterAddShopping = stringPreferencesKey("after_add_shopping")
        val afterShoppingCompleted = stringPreferencesKey("after_shopping_completed")
        val displayEmptyShoppings = booleanPreferencesKey("display_empty_shoppings")
        val swipeProductLeft = stringPreferencesKey("swipe_product_left")
        val swipeProductRight = stringPreferencesKey("swipe_product_right")
        val swipeShoppingLeft = stringPreferencesKey("swipe_shopping_left")
        val swipeShoppingRight = stringPreferencesKey("swipe_shopping_right")
        val archiveAsCompleted = booleanPreferencesKey("archive_as_completed")
    }

    object CodeVersion14 {
        const val firstOpened = "pref_first"
        const val currency = "currency"
        const val displayCurrencyToLeft = "show_currency"
        const val taxRate = "tax_rate"
        const val titleFontSize = "size_main_text"
        const val bodyFontSize = "size_dop_text"
        const val firstLetterUppercase = "capital_text"
        const val columnCount = "cell_text"
        const val sort = "sort_default"
        const val displayMoney = "show_price"
        const val displayTotal = "sum_default"
        const val editProductAfterCompleted = "edit_after_buy"
        const val saveProductToAutocompletes = "auto_text"
    }
}