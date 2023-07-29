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

    suspend fun saveAppPreferences(entity: AppPreferencesEntity) {
        localDataStore.saveAppPreferences(entity)
    }

    suspend fun displayCompletedPurchases(displayCompleted: String) {
        localDataStore.displayCompletedPurchases(displayCompleted)
    }

    suspend fun displayShoppingsProducts(displayProducts: String) {
        localDataStore.displayShoppingsProducts(displayProducts)
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

    suspend fun invertEditProductAfterCompleted() {
        localDataStore.invertEditProductAfterCompleted()
    }

    suspend fun invertSaveProductToAutocompletes() {
        localDataStore.invertSaveProductToAutocompletes()
    }

    suspend fun invertDisplayDefaultAutocompletes() {
        localDataStore.invertDisplayDefaultAutocompletes()
    }

    suspend fun invertCompletedWithCheckbox() {
        localDataStore.invertCompletedWithCheckbox()
    }

    suspend fun invertEnterToSaveProduct() {
        localDataStore.invertEnterToSaveProduct()
    }

    suspend fun invertHighlightCheckbox() {
        localDataStore.invertHighlightCheckbox()
    }

    suspend fun invertDisplayOtherFields() {
        localDataStore.invertDisplayOtherFields()
    }
}