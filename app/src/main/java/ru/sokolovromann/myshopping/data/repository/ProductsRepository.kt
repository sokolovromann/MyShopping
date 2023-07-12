package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.Product
import ru.sokolovromann.myshopping.data.repository.model.Products
import ru.sokolovromann.myshopping.data.repository.model.Sort
import ru.sokolovromann.myshopping.data.repository.model.SortBy

interface ProductsRepository {

    suspend fun getProducts(shoppingUid: String): Flow<Products?>

    suspend fun moveShoppingListToPurchases(uid: String, lastModified: Long)

    suspend fun moveShoppingListToArchive(uid: String, lastModified: Long)

    suspend fun moveShoppingListToTrash(uid: String, lastModified: Long)

    suspend fun completeProduct(uid: String, lastModified: Long)

    suspend fun activeProduct(uid: String, lastModified: Long)

    suspend fun swapProducts(first: Product, second: Product)

    suspend fun swapProducts(products: List<Product>)

    suspend fun deleteProducts(uids: List<String>,shoppingUid: String, lastModified: Long)

    suspend fun deleteShoppingListTotal(uid: String, lastModified: Long)

    suspend fun pinProducts(uids: List<String>, lastModified: Long)

    suspend fun unpinProducts(uids: List<String>, lastModified: Long)

    suspend fun displayAllPurchasesTotal()

    suspend fun displayCompletedPurchasesTotal()

    suspend fun displayActivePurchasesTotal()

    suspend fun invertProductsMultiColumns()

    suspend fun sortProductsBy(shoppingUid: String, sortBy: SortBy, lastModified: Long)

    suspend fun sortProductsAscending(shoppingUid: String, ascending: Boolean, lastModified: Long)

    suspend fun enableProductsAutomaticSorting(shoppingUid: String, sort: Sort, lastModified: Long)

    suspend fun disableProductsAutomaticSorting(shoppingUid: String, sort: Sort, lastModified: Long)
}