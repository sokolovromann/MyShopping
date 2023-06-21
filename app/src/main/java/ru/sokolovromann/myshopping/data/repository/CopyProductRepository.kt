package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.Product
import ru.sokolovromann.myshopping.data.repository.model.ShoppingList
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists

interface CopyProductRepository {

    suspend fun getPurchases(): Flow<ShoppingLists>

    suspend fun getArchive(): Flow<ShoppingLists>

    suspend fun getProducts(uids: List<String>): Flow<List<Product>>

    suspend fun addShoppingList(shoppingList: ShoppingList)

    suspend fun addProducts(products: List<Product>)
}