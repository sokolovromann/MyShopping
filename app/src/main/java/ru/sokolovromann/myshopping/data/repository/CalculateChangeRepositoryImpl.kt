package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.CalculateChange
import javax.inject.Inject

class CalculateChangeRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping
): CalculateChangeRepository {

    private val shoppingListsDao = localDatasource.getShoppingListsDao()
    private val appConfigDao = localDatasource.getAppConfigDao()

    override suspend fun getCalculateChange(
        uid: String?
    ): Flow<CalculateChange> = withContext(AppDispatchers.IO) {
        return@withContext if (uid == null) {
            appConfigDao.getAppConfig().transform {
                val value = mapping.toCalculateChange(null, it)
                emit(value)
            }
        } else {
            shoppingListsDao.getShoppingList(uid).combine(
                flow = appConfigDao.getAppConfig(),
                transform = { entity, appConfigEntity ->
                    mapping.toCalculateChange(entity, appConfigEntity)
                }
            )
        }
    }
}