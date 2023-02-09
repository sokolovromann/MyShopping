package ru.sokolovromann.myshopping.data.local.dao

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.datasource.LocalDataStore
import ru.sokolovromann.myshopping.data.local.entity.ShoppingPreferencesEntity
import javax.inject.Inject

class ArchivePreferencesDao @Inject constructor(
    private val localDataStore: LocalDataStore
) {

    suspend fun getShoppingPreferences(): Flow<ShoppingPreferencesEntity> {
        return localDataStore.getShoppingPreferences()
    }

    suspend fun sortShoppingsBy(sortBy: String) {
        localDataStore.saveShoppingsSortBy(sortBy)
    }

    suspend fun invertShoppingsSort() {
        localDataStore.invertShoppingSortAscending()
    }

    suspend fun displayShoppingsTotal(displayTotal: String) {
        localDataStore.saveShoppingsDisplayTotal(displayTotal)
    }
}