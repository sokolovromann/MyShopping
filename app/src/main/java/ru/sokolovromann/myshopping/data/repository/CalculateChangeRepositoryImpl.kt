package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.CalculateChangeDao
import ru.sokolovromann.myshopping.data.local.dao.ProductsPreferencesDao
import ru.sokolovromann.myshopping.data.repository.model.CalculateChange
import javax.inject.Inject

class CalculateChangeRepositoryImpl @Inject constructor(
    private val calculateChangeDao: CalculateChangeDao,
    private val preferencesDao: ProductsPreferencesDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
): CalculateChangeRepository {

    override suspend fun getCalculateChange(
        uid: String
    ): Flow<CalculateChange?> = withContext(dispatchers.io) {
        return@withContext calculateChangeDao.getShoppingList(uid).combine(
            flow = preferencesDao.getProductsPreferences(),
            transform = { entity, preferencesEntity ->
                if (entity == null) {
                    return@combine null
                }

                mapping.toCalculateChange(entity, preferencesEntity)
            }
        )
    }
}