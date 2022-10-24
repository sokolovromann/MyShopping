package ru.sokolovromann.myshopping.data.local.dao

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.datasource.LocalDataStore
import ru.sokolovromann.myshopping.data.local.entity.EditCurrencySymbolEntity
import javax.inject.Inject

class EditCurrencySymbolDao @Inject constructor(
    private val localDataStore: LocalDataStore
) {

    suspend fun getEditCurrency(): Flow<EditCurrencySymbolEntity> {
        return localDataStore.getEditCurrencySymbol()
    }

    suspend fun editCurrency(currency: String) {
        localDataStore.saveCurrency(currency)
    }
}