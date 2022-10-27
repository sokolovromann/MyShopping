package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists

interface ArchiveRepository {

    suspend fun getShoppingLists(): Flow<ShoppingLists>

    suspend fun moveShoppingListToPurchases(uid: String, lastModified: Long)

    suspend fun moveShoppingListToTrash(uid: String, lastModified: Long)

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