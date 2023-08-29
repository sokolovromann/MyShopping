package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.EditCurrencySymbol
import javax.inject.Inject

class EditCurrencySymbolRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping
) : EditCurrencySymbolRepository {

    private val appConfigDao = localDatasource.getAppConfigDao()

    override suspend fun getEditCurrencySymbol(): Flow<EditCurrencySymbol> = withContext(
        AppDispatchers.IO) {
        return@withContext appConfigDao.getAppConfig().transform{
            val value = mapping.toEditCurrencySymbol(it)
            emit(value)
        }
    }

    override suspend fun editCurrencySymbol(symbol: String): Unit = withContext(AppDispatchers.IO) {
        appConfigDao.saveCurrency(symbol)
    }
}