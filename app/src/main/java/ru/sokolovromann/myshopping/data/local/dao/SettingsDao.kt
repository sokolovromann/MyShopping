package ru.sokolovromann.myshopping.data.local.dao

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.datasource.LocalDataStore
import ru.sokolovromann.myshopping.data.local.entity.SettingsEntity
import ru.sokolovromann.myshopping.data.local.entity.SettingsPreferencesEntity
import javax.inject.Inject

class SettingsDao @Inject constructor(
    private val localDataStore: LocalDataStore
) {

    suspend fun getSettings(): Flow<SettingsEntity> {
        return localDataStore.getSettings()
    }

    suspend fun getSettingsPreferences(): Flow<SettingsPreferencesEntity> {
        return localDataStore.getSettingsPreferences()
    }

    suspend fun displayProductAutocomplete(displayAutocomplete: String) {
        localDataStore.saveProductsDisplayAutocomplete(displayAutocomplete)
    }

    suspend fun fontSizeSelected(fontSize: String) {
        localDataStore.saveFontSize(fontSize)
    }

    suspend fun invertNightTheme() {
        localDataStore.invertNightTheme()
    }

    suspend fun invertCurrencyDisplayToLeft() {
        localDataStore.invertCurrencyDisplayToLeft()
    }

    suspend fun invertDisplayMoney() {
        localDataStore.invertDisplayMoney()
    }

    suspend fun invertFirstLetterUppercase() {
        localDataStore.invertFirstLetterUppercase()
    }

    suspend fun invertShoppingsMultiColumns() {
        localDataStore.invertShoppingsMultiColumns()
    }

    suspend fun invertProductsMultiColumns() {
        localDataStore.invertProductsMultiColumns()
    }

    suspend fun invertProductsEditCompleted() {
        localDataStore.invertProductsEditCompleted()
    }

    suspend fun invertProductsAddLastProduct() {
        localDataStore.invertProductsAddLastProduct()
    }
}