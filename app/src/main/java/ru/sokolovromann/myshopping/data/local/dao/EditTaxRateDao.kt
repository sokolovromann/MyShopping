package ru.sokolovromann.myshopping.data.local.dao

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.datasource.LocalDataStore
import ru.sokolovromann.myshopping.data.local.entity.AppPreferencesEntity
import javax.inject.Inject

class EditTaxRateDao @Inject constructor(
    private val localDataStore: LocalDataStore
) {

    suspend fun getAppPreferences(): Flow<AppPreferencesEntity> {
        return localDataStore.getAppPreferences()
    }

    suspend fun saveTaxRate(taxRate: Float, asPercent: Boolean) {
        localDataStore.saveTaxRate(taxRate)
        localDataStore.saveTaxRateAsPercent(asPercent)
    }
}