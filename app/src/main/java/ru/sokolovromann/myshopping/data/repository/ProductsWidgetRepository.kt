package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.Products
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists

interface ProductsWidgetRepository {

    suspend fun getShoppingLists(): Flow<ShoppingLists>

    suspend fun getProducts(shoppingUid: String): Flow<Products?>

    suspend fun completeProduct(productUid: String, lastModified: Long)

    suspend fun activeProduct(productUid: String, lastModified: Long)
}