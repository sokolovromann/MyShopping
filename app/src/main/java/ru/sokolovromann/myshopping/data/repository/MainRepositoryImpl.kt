package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.MainDao
import ru.sokolovromann.myshopping.data.local.dao.MainPreferencesDao
import ru.sokolovromann.myshopping.data.local.datasource.AppVersion14LocalDatabase
import ru.sokolovromann.myshopping.data.local.datasource.AppVersion14LocalPreferences
import ru.sokolovromann.myshopping.data.local.resources.MainResources
import ru.sokolovromann.myshopping.data.repository.model.*
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val mainDao: MainDao,
    private val preferencesDao: MainPreferencesDao,
    private val resources: MainResources,
    private val appVersion14LocalDatabase: AppVersion14LocalDatabase,
    private val appVersion14Preferences: AppVersion14LocalPreferences,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : MainRepository {

    override suspend fun getMainPreferences(): Flow<MainPreferences> = withContext(dispatchers.io) {
        return@withContext preferencesDao.getMainPreferences().transform {
            val value = mapping.toMainPreferences(
                it,
                appVersion14Preferences.isMigrateFromAppVersion14()
            )
            emit(value)
        }
    }

    override suspend fun getDefaultCurrency(): Flow<Currency> = withContext(dispatchers.io) {
        return@withContext resources.getCurrencyResources().transform {
            val value = mapping.toCurrency(it.defaultCurrency, it.defaultCurrencyDisplayToLeft)
            emit(value)
        }
    }

    override suspend fun getAppVersion14(): Flow<AppVersion14> = withContext(dispatchers.io) {
        val appVersion14 = mapping.toAppVersion14(
            shoppingListsCursor = appVersion14LocalDatabase.getShoppings(),
            productsCursor = appVersion14LocalDatabase.getProducts(),
            autocompletesCursor = appVersion14LocalDatabase.getAutocompletes(),
            preferences = appVersion14Preferences.getAppVersion14Preferences()
        )
        return@withContext flowOf(appVersion14)
    }

    override suspend fun addShoppingList(
        shoppingList: ShoppingList
    ): Unit = withContext(dispatchers.io) {
        val entity = mapping.toShoppingEntity(shoppingList)
        mainDao.insertShopping(entity)
    }

    override suspend fun addProduct(product: Product): Unit = withContext(dispatchers.io) {
        val entity = mapping.toProductEntity(product)
        mainDao.insertProduct(entity)
    }

    override suspend fun addAutocomplete(
        autocomplete: Autocomplete
    ): Unit = withContext(dispatchers.io) {
        val entity = mapping.toAutocompleteEntity(autocomplete)
        mainDao.insertAutocomplete(entity)
    }

    override suspend fun addAppOpenedAction(
        appOpenedAction: AppOpenedAction
    ): Unit = withContext(dispatchers.io) {
        val name = mapping.toAppOpenedActionName(appOpenedAction)
        preferencesDao.addAppOpenedAction(name)
    }

    override suspend fun addCurrency(currency: Currency): Unit = withContext(dispatchers.io) {
        val symbol = mapping.toCurrencySymbol(currency)
        preferencesDao.addCurrency(symbol)

        val displayToLeft = mapping.toCurrencyDisplayToLeft(currency)
        preferencesDao.addCurrencyDisplayToLeft(displayToLeft)
    }

    override suspend fun addTaxRate(taxRate: TaxRate): Unit = withContext(dispatchers.io) {
        val value = mapping.toTaxRateValue(taxRate)
        preferencesDao.addTaxRate(value)

        val asPercent = mapping.toTaxRateAsPercent(taxRate)
        preferencesDao.addTaxRateAsPercent(asPercent)
    }

    override suspend fun addFontSize(fontSize: FontSize): Unit = withContext(dispatchers.io) {
        val name = mapping.toFontSizeName(fontSize)
        preferencesDao.addFontSize(name)
    }

    override suspend fun addFirstLetterUppercase(
        firstLetterUppercase: Boolean
    ): Unit = withContext(dispatchers.io) {
        preferencesDao.addFirstLetterUppercase(firstLetterUppercase)
    }

    override suspend fun addShoppingListsMultiColumns(
        multiColumns: Boolean
    ): Unit = withContext(dispatchers.io) {
        preferencesDao.addShoppingsMultiColumns(multiColumns)
    }

    override suspend fun addDisplayCompleted(
        displayCompleted: DisplayCompleted
    ): Unit = withContext(dispatchers.io) {
        val name = mapping.toDisplayCompletedName(displayCompleted)
        preferencesDao.addDisplayCompleted(name)
    }

    override suspend fun addShoppingListsDisplayTotal(
        displayTotal: DisplayTotal
    ): Unit = withContext(dispatchers.io) {
        val name = mapping.toDisplayTotalName(displayTotal)
        preferencesDao.addShoppingsDisplayTotal(name)
    }

    override suspend fun addProductsMultiColumns(
        multiColumns: Boolean
    ): Unit = withContext(dispatchers.io) {
        preferencesDao.addProductsMultiColumns(multiColumns)
    }

    override suspend fun addProductsDisplayTotal(
        displayTotal: DisplayTotal
    ): Unit = withContext(dispatchers.io) {
        val name = mapping.toDisplayTotalName(displayTotal)
        preferencesDao.addProductsDisplayTotal(name)
    }

    override suspend fun addProductsEditCompleted(
        editCompleted: Boolean
    ): Unit = withContext(dispatchers.io) {
        preferencesDao.addProductsEditCompleted(editCompleted)
    }

    override suspend fun addProductsAddLastProduct(
        addLastProduct: Boolean
    ): Unit = withContext(dispatchers.io) {
        preferencesDao.addProductsAddLastProducts(addLastProduct)
    }

    override suspend fun addDisplayMoney(displayMoney: Boolean): Unit = withContext(dispatchers.io) {
        preferencesDao.addDisplayMoney(displayMoney)
    }

    override suspend fun addScreenSize(screenSize: ScreenSize): Unit = withContext(dispatchers.io) {
        val name = mapping.toScreenSizeName(screenSize)
        preferencesDao.addScreenSize(name)
    }
}