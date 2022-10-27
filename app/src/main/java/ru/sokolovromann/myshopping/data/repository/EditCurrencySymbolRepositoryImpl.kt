package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.EditCurrencySymbolDao
import ru.sokolovromann.myshopping.data.repository.model.EditCurrencySymbol
import javax.inject.Inject

class EditCurrencySymbolRepositoryImpl @Inject constructor(
    private val currencyDao: EditCurrencySymbolDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : EditCurrencySymbolRepository {

    override suspend fun getEditCurrencySymbol(): Flow<EditCurrencySymbol> = withContext(dispatchers.io) {
        return@withContext currencyDao.getEditCurrency().combine(
            flow = currencyDao.getSettingsPreferences(),
            transform = { entity, preferencesEntity ->
                mapping.toEditCurrencySymbol(entity, preferencesEntity)
            }
        )
    }

    override suspend fun editCurrencySymbol(symbol: String): Unit = withContext(dispatchers.io) {
        currencyDao.editCurrency(symbol)
    }
}