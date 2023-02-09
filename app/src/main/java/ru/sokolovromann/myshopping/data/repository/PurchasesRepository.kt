package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.ShoppingList
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists

interface PurchasesRepository {

    suspend fun getShoppingLists(): Flow<ShoppingLists>

    suspend fun addShoppingList(shoppingList: ShoppingList)

    suspend fun moveShoppingListToArchive(uid: String, lastModified: Long)

    suspend fun moveShoppingListToTrash(uid: String, lastModified: Long)

    suspend fun swapShoppingLists(first: ShoppingList, second: ShoppingList)

    suspend fun sortShoppingListsByCreated()

    suspend fun sortShoppingListsByLastModified()

    suspend fun sortShoppingListsByName()

    suspend fun sortShoppingListsByTotal()

    suspend fun displayShoppingListsAllTotal()

    suspend fun displayShoppingListsCompletedTotal()

    suspend fun displayShoppingListsActiveTotal()

    suspend fun invertShoppingListsSort()
}