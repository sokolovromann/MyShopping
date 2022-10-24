package ru.sokolovromann.myshopping.data.local.dao

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.datasource.LocalDataStore
import ru.sokolovromann.myshopping.data.local.entity.EditTaxRateEntity
import javax.inject.Inject

class EditTaxRateDao @Inject constructor(
    private val localDataStore: LocalDataStore
) {

    suspend fun getEditTaxRate(): Flow<EditTaxRateEntity> {
        return localDataStore.getEditTaxRate()
    }

    suspend fun editTaxRate(taxRate: Float, asPercent: Boolean) {
        localDataStore.saveTaxRate(taxRate)
        localDataStore.saveTaxRateAsTaxRate(asPercent)
    }
}