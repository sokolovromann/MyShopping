package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.Products

interface ProductsRepository {

    suspend fun getProducts(shoppingUid: String): Flow<Products?>

    suspend fun completeProduct(uid: String, lastModified: Long)

    suspend fun activeProduct(uid: String, lastModified: Long)

    suspend fun deleteProducts(shoppingUid: String, lastModified: Long)

    suspend fun deleteProduct(shoppingUid: String, productUid: String, lastModified: Long)

    suspend fun sortProductsByCreated()

    suspend fun sortProductsByLastModified()

    suspend fun sortProductsByTotal()

    suspend fun sortProductsByName()

    suspend fun displayProductsCompletedFirst()

    suspend fun displayProductsCompletedLast()

    suspend fun displayProductsAllTotal()

    suspend fun displayProductsCompletedTotal()

    suspend fun displayProductsActiveTotal()

    suspend fun invertProductsSort()

    suspend fun hideProductsCompleted()
}