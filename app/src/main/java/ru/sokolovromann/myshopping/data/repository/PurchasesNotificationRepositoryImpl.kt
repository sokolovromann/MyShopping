package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.PurchasesNotificationDao
import ru.sokolovromann.myshopping.data.local.dao.PurchasesNotificationPreferencesDao
import ru.sokolovromann.myshopping.data.repository.model.ShoppingListNotification
import ru.sokolovromann.myshopping.data.repository.model.ShoppingListNotifications
import javax.inject.Inject

class PurchasesNotificationRepositoryImpl @Inject constructor(
    private val notificationDao: PurchasesNotificationDao,
    private val preferencesDao: PurchasesNotificationPreferencesDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
): PurchasesNotificationRepository {

    override suspend fun getShoppingLists(): Flow<ShoppingListNotifications> = withContext(dispatchers.io) {
        return@withContext notificationDao.getShoppingLists().combine(
            flow = preferencesDao.getShoppingPreferences(),
            transform = { entities, preferencesEntity ->
                mapping.toShoppingListNotifications(entities, preferencesEntity)
            }
        )
    }

    override suspend fun getShoppingList(
        uid: String
    ): Flow<ShoppingListNotification?> = withContext(dispatchers.io) {
        return@withContext notificationDao.getShoppingList(uid).combine(
            flow = preferencesDao.getShoppingPreferences(),
            transform = { entity, preferencesEntity ->
                if (entity == null) {
                    return@combine null
                }

                mapping.toShoppingListNotification(entity, preferencesEntity)
            }
        )
    }

    override suspend fun deleteReminder(
        uid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        notificationDao.deleteReminder(uid, lastModified)
    }
}