package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.EditTaxRateDao
import ru.sokolovromann.myshopping.data.repository.model.EditTaxRate
import ru.sokolovromann.myshopping.data.repository.model.TaxRate
import javax.inject.Inject

class EditTaxRateRepositoryImpl @Inject constructor(
    private val taxRateDao: EditTaxRateDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : EditTaxRateRepository {

    override suspend fun getEditTaxRate(): Flow<EditTaxRate> = withContext(dispatchers.io) {
        return@withContext taxRateDao.getAppPreferences().transform {
            val value = mapping.toEditTaxRate(it)
            emit(value)
        }
    }

    override suspend fun editTaxRate(taxRate: TaxRate): Unit = withContext(dispatchers.io) {
        val value = mapping.toTaxRateValue(taxRate)
        val asPercent = mapping.toTaxRateAsPercent(taxRate)
        taxRateDao.saveTaxRate(value, asPercent)
    }
}