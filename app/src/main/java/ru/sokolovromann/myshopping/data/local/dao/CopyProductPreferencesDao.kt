package ru.sokolovromann.myshopping.data.local.dao

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.datasource.LocalDataStore
import ru.sokolovromann.myshopping.data.local.entity.ShoppingPreferencesEntity
import javax.inject.Inject

class CopyProductPreferencesDao @Inject constructor(
    private val localDataStore: LocalDataStore
) {

    suspend fun getShoppingPreferences(): Flow<ShoppingPreferencesEntity> {
        return localDataStore.getShoppingPreferences()
    }
}