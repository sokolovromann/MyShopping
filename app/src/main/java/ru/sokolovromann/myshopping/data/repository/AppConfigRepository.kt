package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.AppConfig
import ru.sokolovromann.myshopping.data.repository.model.Currency
import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.DisplayProducts
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.EditCurrencySymbol
import ru.sokolovromann.myshopping.data.repository.model.EditTaxRate
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.LockProductElement
import ru.sokolovromann.myshopping.data.repository.model.Money
import ru.sokolovromann.myshopping.data.repository.model.Settings
import ru.sokolovromann.myshopping.data.repository.model.UserPreferencesDefaults
import java.text.DecimalFormat
import javax.inject.Inject

class AppConfigRepository @Inject constructor(localDatasource: LocalDatasource) {

    private val appConfigDao = localDatasource.getAppConfigDao()
    private val resourcesDao = localDatasource.getResourcesDao()

    private val dispatcher = AppDispatchers.IO

    suspend fun getAppConfig(): Flow<AppConfig> = withContext(dispatcher) {
        return@withContext appConfigDao.getAppConfig().map {
            RepositoryMapper.toAppConfig(it)
        }
    }

    suspend fun getDefaultCurrency(): Flow<Currency> = withContext(dispatcher) {
        val currency = resourcesDao.getCurrency()
        val value = RepositoryMapper.toCurrency(currency.defaultCurrency, currency.displayDefaultCurrencyToLeft)
        return@withContext flowOf(value)
    }

    suspend fun getSettings(): Flow<Settings> = withContext(dispatcher) {
        return@withContext appConfigDao.getAppConfig().map {
            val resources = resourcesDao.getSettings()
            RepositoryMapper.toSettings(it, resources)
        }
    }

    suspend fun getEditCurrencySymbol(): Flow<EditCurrencySymbol> = withContext(dispatcher) {
        return@withContext appConfigDao.getAppConfig().map{
            RepositoryMapper.toEditCurrencySymbol(it)
        }
    }

    suspend fun getEditTaxRate(): Flow<EditTaxRate> = withContext(dispatcher) {
        return@withContext appConfigDao.getAppConfig().map {
            RepositoryMapper.toEditTaxRate(it)
        }
    }

    suspend fun saveAppConfig(appConfig: AppConfig): Unit = withContext(dispatcher) {
        val entity = RepositoryMapper.toAppConfigEntity(appConfig)
        appConfigDao.saveAppConfig(entity)
    }

    suspend fun saveFontSize(fontSize: FontSize): Unit = withContext(dispatcher) {
        val value = RepositoryMapper.toFontSizeString(fontSize)
        appConfigDao.saveFontSize(value)
    }

    suspend fun saveCurrencySymbol(symbol: String): Unit = withContext(dispatcher) {
        appConfigDao.saveCurrency(symbol)
    }

    suspend fun saveTaxRate(taxRate: Money): Unit = withContext(dispatcher) {
        val value = RepositoryMapper.toTaxRateValue(taxRate)
        appConfigDao.saveTaxRate(value)
    }

    suspend fun saveMoneyFractionDigits(decimalFormat: DecimalFormat): Unit = withContext(dispatcher) {
        val min = RepositoryMapper.toMinMoneyFractionDigits(decimalFormat)
        appConfigDao.saveMinMoneyFractionDigits(min)

        val max = RepositoryMapper.toMaxMoneyFractionDigits(decimalFormat)
        appConfigDao.saveMaxMoneyFractionDigits(max)
    }

    suspend fun displayCompleted(displayCompleted: DisplayCompleted): Unit = withContext(dispatcher) {
        val value = RepositoryMapper.toDisplayCompletedString(displayCompleted)
        appConfigDao.displayCompleted(value)
    }

    suspend fun displayTotal(displayTotal: DisplayTotal): Unit = withContext(dispatcher) {
        val value = RepositoryMapper.toDisplayTotalString(displayTotal)
        appConfigDao.displayTotal(value)
    }

    suspend fun displayShoppingsProducts(displayProducts: DisplayProducts): Unit = withContext(dispatcher) {
        val value = RepositoryMapper.toDisplayShoppingsProductsString(displayProducts)
        appConfigDao.displayShoppingsProducts(value)
    }

    suspend fun lockProductElement(lockProductElement: LockProductElement): Unit = withContext(dispatcher) {
        val value = RepositoryMapper.toLockProductString(lockProductElement)
        appConfigDao.lockProductElement(value)
    }

    suspend fun invertNightTheme(): Unit = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.NIGHT_THEME
        appConfigDao.invertNightTheme(valueIfNull)
    }

    suspend fun invertDisplayMoney(): Unit = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.DISPLAY_MONEY
        appConfigDao.invertDisplayMoney(valueIfNull)
    }

    suspend fun invertDisplayCurrencyToLeft(): Unit = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.CURRENCY.displayToLeft
        appConfigDao.invertDisplayCurrencyToLeft(valueIfNull)
    }

    suspend fun invertShoppingListsMultiColumns(): Unit = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.MULTI_COLUMNS
        appConfigDao.invertShoppingsMultiColumns(valueIfNull)
    }

    suspend fun invertProductsMultiColumns(): Unit = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.MULTI_COLUMNS
        appConfigDao.invertProductsMultiColumns(valueIfNull)
    }

    suspend fun invertDisplayOtherFields(): Unit = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.DISPLAY_OTHER_FIELDS
        appConfigDao.invertDisplayOtherFields(valueIfNull)
    }

    suspend fun invertColoredCheckbox(): Unit = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.COLORED_CHECKBOX
        appConfigDao.invertColoredCheckbox(valueIfNull)
    }

    suspend fun invertEditProductAfterCompleted(): Unit = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.EDIT_PRODUCT_AFTER_COMPLETED
        appConfigDao.invertEditProductAfterCompleted(valueIfNull)
    }

    suspend fun invertCompletedWithCheckbox(): Unit = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.COMPLETED_WITH_CHECKBOX
        appConfigDao.invertCompletedWithCheckbox(valueIfNull)
    }

    suspend fun invertEnterToSaveProduct(): Unit = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.ENTER_TO_SAVE_PRODUCTS
        appConfigDao.invertEnterToSaveProduct(valueIfNull)
    }

    suspend fun invertDisplayDefaultAutocompletes(): Unit = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.DISPLAY_DEFAULT_AUTOCOMPLETES
        appConfigDao.invertDisplayDefaultAutocompletes(valueIfNull)
    }

    suspend fun invertSaveProductToAutocompletes(): Unit = withContext(dispatcher) {
        val valueIfNull = UserPreferencesDefaults.SAVE_PRODUCT_TO_AUTOCOMPLETES
        appConfigDao.invertSaveProductToAutocompletes(valueIfNull)
    }
}