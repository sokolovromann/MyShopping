package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.EditShoppingListTotal
import ru.sokolovromann.myshopping.data.repository.model.Money
import javax.inject.Inject

class EditShoppingListTotalRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : EditShoppingListTotalRepository {

    private val shoppingListDao = localDatasource.getShoppingListsDao()
    private val appConfigDao = localDatasource.getAppConfigDao()

    override suspend fun getEditShoppingListTotal(
        uid: String?
    ): Flow<EditShoppingListTotal> = withContext(dispatchers.io) {
        return@withContext if (uid == null) {
            appConfigDao.getAppConfig().transform {
                val value = mapping.toEditShoppingListTotal(null, it)
                emit(value)
            }
        } else {
            shoppingListDao.getShoppingList(uid).combine(
                flow = appConfigDao.getAppConfig(),
                transform = { entity, appConfigEntity ->
                    mapping.toEditShoppingListTotal(entity, appConfigEntity)
                }
            )
        }
    }

    override suspend fun saveShoppingListTotal(
        uid: String,
        total: Money,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        val totalToFloat = mapping.toMoneyValue(total)
        shoppingListDao.updateTotal(uid, totalToFloat, lastModified)
    }

    override suspend fun deleteShoppingListTotal(
        uid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        shoppingListDao.deleteTotal(uid, lastModified)
    }
}