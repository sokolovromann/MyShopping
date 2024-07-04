package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.exception.InvalidValueException
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.model.AppBuildConfig
import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.model.SettingsWithConfig
import ru.sokolovromann.myshopping.data.model.Currency
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.LockProductElement
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.NightTheme
import ru.sokolovromann.myshopping.data.model.UserPreferencesDefaults
import ru.sokolovromann.myshopping.data.model.mapper.AppConfigMapper
import javax.inject.Inject

class AppConfigRepository @Inject constructor(localDatasource: LocalDatasource) {

    private val appConfigDao = localDatasource.getAppConfigDao()
    private val resourcesDao = localDatasource.getResourcesDao()

    private val dispatcher = AppDispatchers.IO

    suspend fun getAppConfig(): Flow<AppConfig> = withContext(dispatcher) {
        return@withContext appConfigDao.getAppConfig().map { appConfigEntity ->
            AppConfigMapper.toAppConfig(appConfigEntity)
        }
    }

    suspend fun getDefaultCurrency(): Flow<Currency> = withContext(dispatcher) {
        val currency = resourcesDao.getCurrency()
        val value = Currency(
            symbol = currency.defaultCurrency,
            displayToLeft = currency.displayDefaultCurrencyToLeft
        )
        return@withContext flowOf(value)
    }

    suspend fun getSettingsWithConfig(): Flow<SettingsWithConfig> = withContext(dispatcher) {
        return@withContext appConfigDao.getAppConfig().map { appConfigEntity ->
            AppConfigMapper.toSettingsWithConfig(appConfigEntity)
        }
    }

    suspend fun saveAppConfig(appConfig: AppConfig): Result<Unit> = withContext(dispatcher) {
        val userCodeVersion = appConfig.appBuildConfig.userCodeVersion
        return@withContext if (userCodeVersion <= AppBuildConfig.UNKNOWN_CODE_VERSION) {
            val exception = InvalidValueException("Unknown code version")
            Result.failure(exception)
        } else {
            val entity = AppConfigMapper.toAppConfigEntity(appConfig)
            appConfigDao.saveAppConfig(entity)

            Result.success(Unit)
        }
    }

    suspend fun saveUserCodeVersion(userCodeVersion: Int): Result<Unit> = withContext(dispatcher) {
        return@withContext if (userCodeVersion <= AppBuildConfig.UNKNOWN_CODE_VERSION) {
            val exception = InvalidValueException("Unknown code version")
            Result.failure(exception)
        } else {
            appConfigDao.saveUserCodeVersion(userCodeVersion)
            Result.success(Unit)
        }
    }

    suspend fun saveFontSize(
        appFontSize: FontSize,
        widgetFontSize: FontSize
    ): Result<Unit> = withContext(dispatcher) {
        appConfigDao.saveFontSize(appFontSize.name, widgetFontSize.name)
        return@withContext Result.success(Unit)
    }

    suspend fun saveCurrencySymbol(symbol: String): Result<Unit> = withContext(dispatcher) {
        appConfigDao.saveCurrency(symbol)
        return@withContext Result.success(Unit)
    }

    suspend fun saveTaxRate(taxRate: Money): Result<Unit> = withContext(dispatcher) {
        appConfigDao.saveTaxRate(taxRate.value)
        return@withContext Result.success(Unit)
    }

    suspend fun saveMaxAutocompletes(
        maxNames: Int,
        maxQuantities: Int,
        maxMoneys: Int,
        maxOthers: Int
    ): Result<Unit> = withContext(dispatcher) {
        appConfigDao.saveMaxAutocompleteNames(maxNames)
        appConfigDao.saveMaxAutocompleteQuantities(maxQuantities)
        appConfigDao.saveMaxAutocompleteMoneys(maxMoneys)
        appConfigDao.saveMaxAutocompleteOthers(maxOthers)
        return@withContext Result.success(Unit)
    }

    suspend fun displayCompleted(
        appDisplayCompleted: DisplayCompleted,
        widgetDisplayCompleted: DisplayCompleted
    ): Result<Unit> = withContext(dispatcher) {
        appConfigDao.displayCompleted(
            appDisplayCompleted = appDisplayCompleted.name,
            widgetDisplayCompleted = widgetDisplayCompleted.name
        )
        return@withContext Result.success(Unit)
    }

    suspend fun displayTotal(
        displayTotal: DisplayTotal
    ): Result<Unit> = withContext(dispatcher) {
        appConfigDao.displayTotal(displayTotal.name)
        return@withContext Result.success(Unit)
    }

    suspend fun displayShoppingsProducts(
        displayProducts: DisplayProducts
    ): Result<Unit> = withContext(dispatcher) {
        appConfigDao.displayShoppingsProducts(displayProducts.name)
        return@withContext Result.success(Unit)
    }

    suspend fun lockProductElement(
        lockProductElement: LockProductElement
    ): Result<Unit> = withContext(dispatcher) {
        appConfigDao.lockProductElement(lockProductElement.name)
        return@withContext Result.success(Unit)
    }

    suspend fun saveNightTheme(nightTheme: NightTheme): Result<Unit> = withContext(dispatcher) {
        appConfigDao.saveNightTheme(
            appNightTheme = nightTheme.isAppNightTheme(),
            widgetNightTheme = nightTheme.isWidgetNightTheme()
        )
        return@withContext Result.success(Unit)
    }

    suspend fun invertLongTotal(): Result<Unit> = withContext(dispatcher) {
        val valueIfNull = !UserPreferencesDefaults.DISPLAY_LONG_TOTAL
        appConfigDao.invertLongTotal(valueIfNull)

        return@withContext Result.success(Unit)
    }

    suspend fun invertDisplayMoney(): Result<Unit> = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.DISPLAY_MONEY
        appConfigDao.invertDisplayMoney(valueIfNull)

        return@withContext Result.success(Unit)
    }

    suspend fun invertDisplayCurrencyToLeft(): Result<Unit> = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.getCurrency().displayToLeft
        appConfigDao.invertDisplayCurrencyToLeft(valueIfNull)

        return@withContext Result.success(Unit)
    }

    suspend fun invertDisplayMoneyZeros(): Result<Unit> = withContext(dispatcher) {
        val minMoneyFractionDigits = appConfigDao.getAppConfig().firstOrNull()?.userPreferences
            ?.minMoneyFractionDigits

        val value = if (minMoneyFractionDigits == null || minMoneyFractionDigits <= 0) {
            UserPreferencesDefaults.getMoneyDecimalFormat().minimumFractionDigits
        } else {
            0
        }
        appConfigDao.saveMinMoneyFractionDigits(value)

        return@withContext Result.success(Unit)
    }

    suspend fun invertShoppingListsMultiColumns(): Result<Unit> = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.MULTI_COLUMNS
        appConfigDao.invertShoppingsMultiColumns(valueIfNull)

        return@withContext Result.success(Unit)
    }

    suspend fun invertProductsMultiColumns(): Result<Unit> = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.MULTI_COLUMNS
        appConfigDao.invertProductsMultiColumns(valueIfNull)

        return@withContext Result.success(Unit)
    }

    suspend fun invertStrikethroughCompletedProducts(): Result<Unit> = withContext(dispatcher) {
        val valueIfNull = !UserPreferencesDefaults.STRIKETHROUGH_COMPLETED_PRODUCTS
        appConfigDao.invertStrikethroughCompletedProducts(valueIfNull)

        return@withContext Result.success(Unit)
    }

    suspend fun invertDisplayOtherFields(): Result<Unit> = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.DISPLAY_OTHER_FIELDS
        appConfigDao.invertDisplayOtherFields(valueIfNull)

        return@withContext Result.success(Unit)
    }

    suspend fun invertColoredCheckbox(): Result<Unit> = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.COLORED_CHECKBOX
        appConfigDao.invertColoredCheckbox(valueIfNull)

        return@withContext Result.success(Unit)
    }

    suspend fun invertEditProductAfterCompleted(): Result<Unit> = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.EDIT_PRODUCT_AFTER_COMPLETED
        appConfigDao.invertEditProductAfterCompleted(valueIfNull)

        return@withContext Result.success(Unit)
    }

    suspend fun invertCompletedWithCheckbox(): Result<Unit> = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.COMPLETED_WITH_CHECKBOX
        appConfigDao.invertCompletedWithCheckbox(valueIfNull)

        return@withContext Result.success(Unit)
    }

    suspend fun invertEnterToSaveProduct(): Result<Unit> = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.ENTER_TO_SAVE_PRODUCTS
        appConfigDao.invertEnterToSaveProduct(valueIfNull)

        return@withContext Result.success(Unit)
    }

    suspend fun invertDisplayDefaultAutocompletes(): Result<Unit> = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.DISPLAY_DEFAULT_AUTOCOMPLETES
        appConfigDao.invertDisplayDefaultAutocompletes(valueIfNull)

        return@withContext Result.success(Unit)
    }

    suspend fun invertSaveProductToAutocompletes(): Result<Unit> = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.SAVE_PRODUCT_TO_AUTOCOMPLETES
        appConfigDao.invertSaveProductToAutocompletes(valueIfNull)

        return@withContext Result.success(Unit)
    }

    suspend fun invertAutomaticallyEmptyTrash(): Result<Unit> = withContext(dispatcher) {
        val valueIfNull = !UserPreferencesDefaults.AUTOMATICALLY_EMPTY_TRASH
        appConfigDao.invertAutomaticallyEmptyTrash(valueIfNull)

        return@withContext Result.success(Unit)
    }
}