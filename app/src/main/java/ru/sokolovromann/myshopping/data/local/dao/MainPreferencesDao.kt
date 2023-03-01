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

    suspend fun addMainPreferences(mainPreferencesEntity: MainPreferencesEntity) {
        localDataStore.addMainPreferences(mainPreferencesEntity)
    }
}