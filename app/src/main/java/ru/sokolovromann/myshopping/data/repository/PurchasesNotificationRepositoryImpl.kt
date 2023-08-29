package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.ShoppingListNotification
import ru.sokolovromann.myshopping.data.repository.model.ShoppingListNotifications
import javax.inject.Inject

class PurchasesNotificationRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
): PurchasesNotificationRepository {

    private val shoppingListsDao = localDatasource.getShoppingListsDao()
    private val appConfigDao = localDatasource.getAppConfigDao()

    override suspend fun getShoppingLists(): Flow<ShoppingListNotifications> = withContext(dispatchers.io) {
        return@withContext shoppingListsDao.getReminders().combine(
            flow = appConfigDao.getAppConfig(),
            transform = { entities, appConfigEntity ->
                mapping.toShoppingListNotifications(entities, appConfigEntity)
            }
        )
    }

    override suspend fun getShoppingList(
        uid: String
    ): Flow<ShoppingListNotification?> = withContext(dispatchers.io) {
        return@withContext shoppingListsDao.getShoppingList(uid).combine(
            flow = appConfigDao.getAppConfig(),
            transform = { entity, appConfigEntity ->
                if (entity == null) {
                    return@combine null
                }

                mapping.toShoppingListNotification(entity, appConfigEntity)
            }
        )
    }

    override suspend fun deleteReminder(
        uid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        shoppingListsDao.deleteReminder(uid, lastModified)
    }
}