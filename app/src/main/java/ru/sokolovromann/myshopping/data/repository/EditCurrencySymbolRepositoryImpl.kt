package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.AppConfigDao
import ru.sokolovromann.myshopping.data.repository.model.EditCurrencySymbol
import javax.inject.Inject

class EditCurrencySymbolRepositoryImpl @Inject constructor(
    private val appConfigDao: AppConfigDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : EditCurrencySymbolRepository {

    override suspend fun getEditCurrencySymbol(): Flow<EditCurrencySymbol> = withContext(dispatchers.io) {
        return@withContext appConfigDao.getAppConfig().transform{
            val value = mapping.toEditCurrencySymbol(it)
            emit(value)
        }
    }

    override suspend fun editCurrencySymbol(symbol: String): Unit = withContext(dispatchers.io) {
        appConfigDao.saveCurrency(symbol)
    }
}