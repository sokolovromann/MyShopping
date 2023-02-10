package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.Product
import ru.sokolovromann.myshopping.data.repository.model.Products

interface ProductsRepository {

    suspend fun getProducts(shoppingUid: String): Flow<Products?>

    suspend fun completeProduct(uid: String, lastModified: Long)

    suspend fun activeProduct(uid: String, lastModified: Long)

    suspend fun swapProducts(first: Product, second: Product)

    suspend fun swapProducts(products: List<Product>)

    suspend fun hideProducts(shoppingUid: String, lastModified: Long)

    suspend fun hideProduct(shoppingUid: String, productUid: String, lastModified: Long)

    suspend fun displayProductsAllTotal()

    suspend fun displayProductsCompletedTotal()

    suspend fun displayProductsActiveTotal()
}