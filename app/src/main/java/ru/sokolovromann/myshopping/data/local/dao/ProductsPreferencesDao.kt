package ru.sokolovromann.myshopping.data.local.dao

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.datasource.LocalDataStore
import ru.sokolovromann.myshopping.data.local.entity.ProductPreferencesEntity
import javax.inject.Inject

class ProductsPreferencesDao @Inject constructor(
    private val localDataStore: LocalDataStore
) {

    suspend fun getProductsPreferences(): Flow<ProductPreferencesEntity> {
        return localDataStore.getProductPreferences()
    }

    suspend fun sortProductsBy(sortBy: String) {
        localDataStore.saveProductsSortBy(sortBy)
    }

    suspend fun invertProductsSort() {
        localDataStore.invertProductsSortAscending()
    }

    suspend fun displayProductsCompleted(displayCompleted: String) {
        localDataStore.saveProductsDisplayCompleted(displayCompleted)
    }

    suspend fun displayProductsTotal(displayTotal: String) {
        localDataStore.saveProductsDisplayTotal(displayTotal)
    }
}