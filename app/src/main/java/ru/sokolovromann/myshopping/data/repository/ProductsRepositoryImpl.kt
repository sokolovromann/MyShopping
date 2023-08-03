package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.ProductsDao
import ru.sokolovromann.myshopping.data.local.dao.ProductsPreferencesDao
import ru.sokolovromann.myshopping.data.repository.model.*
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(
    private val productsDao: ProductsDao,
    private val preferencesDao: ProductsPreferencesDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : ProductsRepository {

    override suspend fun getProducts(shoppingUid: String): Flow<Products?> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = productsDao.getShoppingList(shoppingUid),
            flow2 = productsDao.getShoppingsLastPosition(),
            flow3 = preferencesDao.getAppPreferences(),
            transform = { entity, lastPosition, preferencesEntity ->
                if (entity == null) {
                    return@combine null
                }

                mapping.toProducts(entity, lastPosition, preferencesEntity)
            }
        )
    }

    override suspend fun moveShoppingListToPurchases(
        uid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        productsDao.moveShoppingToPurchases(uid, lastModified)
    }

    override suspend fun moveShoppingListToArchive(
        uid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        productsDao.moveShoppingToArchive(uid, lastModified)
    }

    override suspend fun moveShoppingListToTrash(
        uid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        productsDao.moveShoppingToTrash(uid, lastModified)
    }

    override suspend fun copyShoppingList(
        shoppingList: ShoppingList
    ): Unit = withContext(dispatchers.io) {
        val shoppingEntity = mapping.toShoppingEntity(shoppingList)
        productsDao.insertShopping(shoppingEntity)

        shoppingList.products.forEach { product ->
            val productEntity = mapping.toProductEntity(product)
            productsDao.insertProduct(productEntity)
        }
    }

    override suspend fun completeProduct(
        uid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        productsDao.completeProduct(uid, lastModified)
    }

    override suspend fun activeProduct(
        uid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        productsDao.activeProduct(uid, lastModified)
    }

    override suspend fun swapProducts(
        first: Product,
        second: Product
    ): Unit = withContext(dispatchers.io) {
        productsDao.updateProductPosition(first.productUid, first.position, first.lastModified)
        productsDao.updateProductPosition(second.productUid, second.position, second.lastModified)
        productsDao.updateShoppingLastModified(first.shoppingUid, first.lastModified)
    }

    override suspend fun swapProducts(products: List<Product>): Unit = withContext(dispatchers.io) {
        val entities = mapping.toProductEntities(products)
        productsDao.updateProducts(entities)

        val firstProduct = products.first()
        productsDao.updateShoppingLastModified(
            uid = firstProduct.shoppingUid,
            lastModified = firstProduct.lastModified
        )
    }

    override suspend fun deleteProducts(
        uids: List<String>,
        shoppingUid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        uids.forEach { productsDao.deleteProduct(it) }
        productsDao.updateShoppingLastModified(shoppingUid, lastModified)
    }

    override suspend fun deleteShoppingListTotal(
        uid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        productsDao.deleteShoppingTotal(uid, lastModified)
    }

    override suspend fun pinProducts(
        uids: List<String>,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        uids.forEach { productsDao.pinProduct(it, lastModified) }
    }

    override suspend fun unpinProducts(
        uids: List<String>,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        uids.forEach { productsDao.unpinProduct(it, lastModified) }
    }

    override suspend fun displayAllPurchasesTotal(): Unit = withContext(dispatchers.io) {
        val displayTotal = mapping.toDisplayTotalName(DisplayTotal.ALL)
        preferencesDao.displayPurchasesTotal(displayTotal)
    }

    override suspend fun displayCompletedPurchasesTotal(): Unit = withContext(dispatchers.io) {
        val displayTotal = mapping.toDisplayTotalName(DisplayTotal.COMPLETED)
        preferencesDao.displayPurchasesTotal(displayTotal)
    }

    override suspend fun displayActivePurchasesTotal(): Unit = withContext(dispatchers.io) {
        val displayTotal = mapping.toDisplayTotalName(DisplayTotal.ACTIVE)
        preferencesDao.displayPurchasesTotal(displayTotal)
    }

    override suspend fun invertProductsMultiColumns(): Unit = withContext(dispatchers.io) {
        preferencesDao.invertProductsMultiColumns()
    }

    override suspend fun sortProductsBy(
        shoppingUid: String,
        sortBy: SortBy,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        val sortByName = mapping.toSortByName(sortBy)
        productsDao.sortProductsBy(shoppingUid, sortByName, lastModified)
    }

    override suspend fun sortProductsAscending(
        shoppingUid: String,
        ascending: Boolean,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        productsDao.sortProductsAscending(shoppingUid, ascending, lastModified)
    }

    override suspend fun enableProductsAutomaticSorting(
        shoppingUid: String,
        sort: Sort,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        val sortBy = mapping.toSortByName(sort.sortBy)
        productsDao.enableProductsAutomaticSorting(shoppingUid, sortBy, sort.ascending, lastModified)
    }

    override suspend fun disableProductsAutomaticSorting(
        shoppingUid: String,
        sort: Sort,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        val sortBy = mapping.toSortByName(sort.sortBy)
        productsDao.disableProductsAutomaticSorting(shoppingUid, sortBy, sort.ascending, lastModified)
    }
}