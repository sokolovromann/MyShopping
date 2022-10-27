package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.ShoppingListNotification
import ru.sokolovromann.myshopping.data.repository.model.ShoppingListNotifications

interface PurchasesNotificationRepository {

    suspend fun getShoppingLists(): Flow<ShoppingListNotifications>

    suspend fun getShoppingList(uid: String): Flow<ShoppingListNotification?>

    suspend fun deleteReminder(uid: String, lastModified: Long)
}