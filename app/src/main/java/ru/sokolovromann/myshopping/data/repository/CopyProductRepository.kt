package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.Product
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists

interface CopyProductRepository {

    suspend fun getPurchases(): Flow<ShoppingLists>

    suspend fun getArchive(): Flow<ShoppingLists>

    suspend fun getTrash(): Flow<ShoppingLists>

    suspend fun getProduct(uid: String): Flow<Product?>

    suspend fun addProduct(product: Product)
}