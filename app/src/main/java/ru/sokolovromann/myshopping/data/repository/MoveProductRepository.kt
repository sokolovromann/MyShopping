package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists

interface MoveProductRepository {

    suspend fun getPurchases(): Flow<ShoppingLists>

    suspend fun getArchive(): Flow<ShoppingLists>

    suspend fun getTrash(): Flow<ShoppingLists>

    suspend fun moveProduct(productUid: String, shoppingUid: String, lastModified: Long)
}