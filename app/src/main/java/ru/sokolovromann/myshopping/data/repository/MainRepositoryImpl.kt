package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.MainPreferencesDao
import ru.sokolovromann.myshopping.data.local.resources.MainResources
import ru.sokolovromann.myshopping.data.repository.model.*
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val preferencesDao: MainPreferencesDao,
    private val resources: MainResources,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : MainRepository {

    override suspend fun getMainPreferences(): Flow<MainPreferences> = withContext(dispatchers.io) {
        return@withContext preferencesDao.getMainPreferences().transform {
            mapping.toMainPreferences(it)
        }
    }

    override suspend fun getDefaultCurrency(): Flow<Currency> = withContext(dispatchers.io) {
        return@withContext resources.getCurrencyResources().transform {
            mapping.toCurrency(it.defaultCurrency, it.defaultCurrencyDisplayToLeft)
        }
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

    override suspend fun addShoppingListsSort(sort: Sort): Unit = withContext(dispatchers.io) {
        val sortBy = mapping.toSortByName(sort)
        preferencesDao.addShoppingsBySort(sortBy)

        val ascending = mapping.toSortAscending(sort)
        preferencesDao.addShoppingsSortAscending(ascending)
    }

    override suspend fun addShoppingListsDisplayCompleted(
        displayCompleted: DisplayCompleted
    ): Unit = withContext(dispatchers.io) {
        val name = mapping.toDisplayCompletedName(displayCompleted)
        preferencesDao.addShoppingsDisplayCompleted(name)
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

    override suspend fun addProductsSort(sort: Sort): Unit = withContext(dispatchers.io) {
        val sortBy = mapping.toSortByName(sort)
        preferencesDao.addProductsBySort(sortBy)

        val ascending = mapping.toSortAscending(sort)
        preferencesDao.addProductsSortAscending(ascending)
    }

    override suspend fun addProductsDisplayCompleted(
        displayCompleted: DisplayCompleted
    ): Unit = withContext(dispatchers.io) {
        val name = mapping.toDisplayCompletedName(displayCompleted)
        preferencesDao.addProductsDisplayCompleted(name)
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