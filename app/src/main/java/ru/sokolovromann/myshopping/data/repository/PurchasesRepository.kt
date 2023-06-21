package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.ShoppingList
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists

interface PurchasesRepository {

    suspend fun getShoppingLists(): Flow<ShoppingLists>

    suspend fun getShoppingListsLastPosition(): Flow<Int?>

    suspend fun addShoppingList(shoppingList: ShoppingList)

    suspend fun moveShoppingListsToArchive(uids: List<String>, lastModified: Long)

    suspend fun moveShoppingListsToTrash(uids: List<String>, lastModified: Long)

    suspend fun swapShoppingLists(first: ShoppingList, second: ShoppingList)

    suspend fun swapShoppingLists(shoppingLists: List<ShoppingList>)

    suspend fun displayAllPurchasesTotal()

    suspend fun displayCompletedPurchasesTotal()

    suspend fun displayActivePurchasesTotal()

    suspend fun invertShoppingListsMultiColumns()
}