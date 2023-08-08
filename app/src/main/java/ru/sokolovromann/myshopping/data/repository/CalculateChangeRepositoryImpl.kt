package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.AppConfigDao
import ru.sokolovromann.myshopping.data.local.dao.CalculateChangeDao
import ru.sokolovromann.myshopping.data.repository.model.CalculateChange
import javax.inject.Inject

class CalculateChangeRepositoryImpl @Inject constructor(
    private val calculateChangeDao: CalculateChangeDao,
    private val appConfigDao: AppConfigDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
): CalculateChangeRepository {

    override suspend fun getCalculateChange(
        uid: String?
    ): Flow<CalculateChange> = withContext(dispatchers.io) {
        return@withContext if (uid == null) {
            appConfigDao.getAppConfig().transform {
                val value = mapping.toCalculateChange(null, it)
                emit(value)
            }
        } else {
            calculateChangeDao.getShoppingList(uid).combine(
                flow = appConfigDao.getAppConfig(),
                transform = { entity, appConfigEntity ->
                    mapping.toCalculateChange(entity, appConfigEntity)
                }
            )
        }
    }
}