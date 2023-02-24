package ru.sokolovromann.myshopping.data.local.dao

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.datasource.LocalDataStore
import ru.sokolovromann.myshopping.data.local.entity.ProductPreferencesEntity
import javax.inject.Inject

class AddEditProductPreferencesDao @Inject constructor(
    private val localDataStore: LocalDataStore
) {

    suspend fun getProductPreferences(): Flow<ProductPreferencesEntity> {
        return localDataStore.getProductPreferences()
    }

    suspend fun saveProductsProductLock(productLock: String) {
        localDataStore.saveProductsProductLock(productLock)
    }
}