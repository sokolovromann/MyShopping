package ru.sokolovromann.myshopping.data.local.dao

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.datasource.LocalDataStore
import ru.sokolovromann.myshopping.data.local.entity.MainPreferencesEntity
import javax.inject.Inject

class MainPreferencesDao @Inject constructor(
    private val localDataStore: LocalDataStore
) {

    suspend fun getMainPreferences(): Flow<MainPreferencesEntity> {
        return localDataStore.getMainPreferences()
    }

    suspend fun addAppOpenedAction(appOpenedAction: String) {
        localDataStore.saveAppOpenedAction(appOpenedAction)
    }

    suspend fun addCurrency(currency: String) {
        localDataStore.saveCurrency(currency)
    }

    suspend fun addCurrencyDisplayToLeft(displayToLeft: Boolean) {
        localDataStore.saveCurrencyDisplayToLeft(displayToLeft)
    }

    suspend fun addTaxRate(taxRate: Float) {
        localDataStore.saveTaxRate(taxRate)
    }

    suspend fun addTaxRateAsPercent(asPercent: Boolean) {
        localDataStore.saveTaxRateAsTaxRate(asPercent)
    }

    suspend fun addFontSize(fontSize: String) {
        localDataStore.saveFontSize(fontSize)
    }

    suspend fun addFirstLetterUppercase(firstLetterUppercase: Boolean) {
        localDataStore.saveFirstLetterUppercase(firstLetterUppercase)
    }

    suspend fun addShoppingsMultiColumns(multiColumns: Boolean) {
        localDataStore.saveShoppingsMultiColumns(multiColumns)
    }

    suspend fun addShoppingsBySort(sortBy: String) {
        localDataStore.saveShoppingsSortBy(sortBy)
    }

    suspend fun addShoppingsSortAscending(ascending: Boolean) {
        localDataStore.saveShoppingsSortAscending(ascending)
    }

    suspend fun addShoppingsDisplayCompleted(displayCompleted: String) {
        localDataStore.saveShoppingsDisplayCompleted(displayCompleted)
    }

    suspend fun addShoppingsDisplayTotal(displayTotal: String) {
        localDataStore.saveShoppingsDisplayTotal(displayTotal)
    }

    suspend fun addProductsMultiColumns(multiColumns: Boolean) {
        localDataStore.saveProductsMultiColumns(multiColumns)
    }

    suspend fun addProductsBySort(sortBy: String) {
        localDataStore.saveProductsSortBy(sortBy)
    }

    suspend fun addProductsSortAscending(ascending: Boolean) {
        localDataStore.saveProductsSortAscending(ascending)
    }

    suspend fun addProductsDisplayCompleted(displayCompleted: String) {
        localDataStore.saveProductsDisplayCompleted(displayCompleted)
    }

    suspend fun addProductsDisplayTotal(displayTotal: String) {
        localDataStore.saveProductsDisplayTotal(displayTotal)
    }

    suspend fun addProductsEditCompleted(editCompleted: Boolean) {
        localDataStore.saveProductsEditCompleted(editCompleted)
    }

    suspend fun addProductsAddLastProducts(addLastProduct: Boolean) {
        localDataStore.saveProductsAddLastProduct(addLastProduct)
    }

    suspend fun addDisplayMoney(displayMoney: Boolean) {
        localDataStore.saveDisplayMoney(displayMoney)
    }

    suspend fun addScreenSize(screenSize: String) {
        localDataStore.saveScreenSize(screenSize)
    }
}