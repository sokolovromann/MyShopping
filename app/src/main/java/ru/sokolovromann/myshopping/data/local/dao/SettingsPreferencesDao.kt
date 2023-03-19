package ru.sokolovromann.myshopping.data.local.dao

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.datasource.LocalDataStore
import ru.sokolovromann.myshopping.data.local.entity.AppPreferencesEntity
import javax.inject.Inject

class SettingsPreferencesDao @Inject constructor(
    private val localDataStore: LocalDataStore
) {

    suspend fun getAppPreferences(): Flow<AppPreferencesEntity> {
        return localDataStore.getAppPreferences()
    }

    suspend fun displayCompletedPurchases(displayCompleted: String) {
        localDataStore.displayCompletedPurchases(displayCompleted)
    }

    suspend fun saveFontSize(fontSize: String) {
        localDataStore.saveFontSize(fontSize)
    }

    suspend fun invertNightTheme() {
        localDataStore.invertNightTheme()
    }

    suspend fun invertDisplayCurrencyToLeft() {
        localDataStore.invertDisplayCurrencyToLeft()
    }

    suspend fun invertDisplayMoney() {
        localDataStore.invertDisplayMoney()
    }

    suspend fun invertShoppingsMultiColumns() {
        localDataStore.invertShoppingsMultiColumns()
    }

    suspend fun invertProductsMultiColumns() {
        localDataStore.invertProductsMultiColumns()
    }

    suspend fun invertEditProductAfterCompleted() {
        localDataStore.invertEditProductAfterCompleted()
    }

    suspend fun invertSaveProductToAutocompletes() {
        localDataStore.invertSaveProductToAutocompletes()
    }

    suspend fun invertDisplayDefaultAutocompletes() {
        localDataStore.invertDisplayDefaultAutocompletes()
    }
}