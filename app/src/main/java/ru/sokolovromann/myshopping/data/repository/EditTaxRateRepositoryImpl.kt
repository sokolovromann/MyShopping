package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.EditTaxRate
import ru.sokolovromann.myshopping.data.repository.model.Money
import javax.inject.Inject

class EditTaxRateRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : EditTaxRateRepository {

    private val appConfigDao = localDatasource.getAppConfigDao()

    override suspend fun getEditTaxRate(): Flow<EditTaxRate> = withContext(dispatchers.io) {
        return@withContext appConfigDao.getAppConfig().transform {
            val value = mapping.toEditTaxRate(it)
            emit(value)
        }
    }

    override suspend fun editTaxRate(taxRate: Money): Unit = withContext(dispatchers.io) {
        val value = mapping.toTaxRateValue(taxRate)
        appConfigDao.saveTaxRate(value)

        val asPercent = mapping.toTaxRateAsPercent(taxRate)
        appConfigDao.saveTaxRateAsPercent(asPercent)
    }
}