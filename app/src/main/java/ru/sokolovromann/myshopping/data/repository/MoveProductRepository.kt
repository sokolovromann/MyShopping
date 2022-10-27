package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists

interface MoveProductRepository {

    suspend fun getShoppingLists(): Flow<ShoppingLists>

    suspend fun moveProduct(productUid: String, shoppingUid: String, lastModified: Long)
}