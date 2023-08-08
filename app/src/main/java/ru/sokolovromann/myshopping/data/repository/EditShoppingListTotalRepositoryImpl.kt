package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.AppConfigDao
import ru.sokolovromann.myshopping.data.local.dao.EditShoppingListTotalDao
import ru.sokolovromann.myshopping.data.repository.model.EditShoppingListTotal
import ru.sokolovromann.myshopping.data.repository.model.Money
import javax.inject.Inject

class EditShoppingListTotalRepositoryImpl @Inject constructor(
    private val shoppingListDao: EditShoppingListTotalDao,
    private val appConfigDao: AppConfigDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : EditShoppingListTotalRepository {

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
        shoppingListDao.updateShoppingTotal(uid, totalToFloat, lastModified)
    }

    override suspend fun deleteShoppingListTotal(
        uid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        shoppingListDao.deleteShoppingTotal(uid, lastModified)
    }
}