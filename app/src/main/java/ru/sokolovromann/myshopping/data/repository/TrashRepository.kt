package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists

interface TrashRepository {

    suspend fun getShoppingLists(): Flow<ShoppingLists>

    suspend fun moveShoppingListToPurchases(uid: String, lastModified: Long)

    suspend fun moveShoppingListToArchive(uid: String, lastModified: Long)

    suspend fun deleteShoppingLists()

    suspend fun deleteShoppingList(uid: String)

    suspend fun sortShoppingListsByCreated()

    suspend fun sortShoppingListsByLastModified()

    suspend fun sortShoppingListsByName()

    suspend fun sortShoppingListsByTotal()

    suspend fun displayShoppingListsCompletedFirst()

    suspend fun displayShoppingListsCompletedLast()

    suspend fun displayShoppingListsAllTotal()

    suspend fun displayShoppingListsCompletedTotal()

    suspend fun displayShoppingListsActiveTotal()

    suspend fun invertShoppingListsSort()

    suspend fun hideShoppingListsCompleted()
}