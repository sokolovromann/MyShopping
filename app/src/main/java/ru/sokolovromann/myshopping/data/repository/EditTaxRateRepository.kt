package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.EditTaxRate
import ru.sokolovromann.myshopping.data.repository.model.TaxRate

interface EditTaxRateRepository {

    suspend fun getEditTaxRate(): Flow<EditTaxRate>

    suspend fun editTaxRate(taxRate: TaxRate)
}