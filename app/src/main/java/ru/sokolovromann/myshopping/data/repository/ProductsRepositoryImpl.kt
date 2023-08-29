package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.*
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping
) : ProductsRepository {

    private val shoppingListsDao = localDatasource.getShoppingListsDao()
    private val productsDao = localDatasource.getProductsDao()
    private val appConfigDao = localDatasource.getAppConfigDao()

    override suspend fun getProducts(shoppingUid: String): Flow<Products?> = withContext(AppDispatchers.IO) {
        return@withContext combine(
            flow = shoppingListsDao.getShoppingList(shoppingUid),
            flow2 = shoppingListsDao.getLastPosition(),
            flow3 = appConfigDao.getAppConfig(),
            transform = { entity, lastPosition, appConfigEntity ->
                if (entity == null) {
                    return@combine null
                }

                mapping.toProducts(entity, lastPosition, appConfigEntity)
            }
        )
    }

    override suspend fun moveShoppingListToPurchases(
        uid: String,
        lastModified: Long
    ): Unit = withContext(AppDispatchers.IO) {
        shoppingListsDao.moveToPurchases(uid, lastModified)
    }

    override suspend fun moveShoppingListToArchive(
        uid: String,
        lastModified: Long
    ): Unit = withContext(AppDispatchers.IO) {
        shoppingListsDao.moveToArchive(uid, lastModified)
    }

    override suspend fun moveShoppingListToTrash(
        uid: String,
        lastModified: Long
    ): Unit = withContext(AppDispatchers.IO) {
        shoppingListsDao.moveToTrash(uid, lastModified)
    }

    override suspend fun copyShoppingList(
        shoppingList: ShoppingList
    ): Unit = withContext(AppDispatchers.IO) {
        val shoppingEntity = mapping.toShoppingEntity(shoppingList)
        shoppingListsDao.insertShopping(shoppingEntity)

        shoppingList.products.forEach { product ->
            val productEntity = mapping.toProductEntity(product)
            productsDao.insertProduct(productEntity)
        }
    }

    override suspend fun completeProduct(
        uid: String,
        lastModified: Long
    ): Unit = withContext(AppDispatchers.IO) {
        productsDao.completeProduct(uid, lastModified)
    }

    override suspend fun activeProduct(
        uid: String,
        lastModified: Long
    ): Unit = withContext(AppDispatchers.IO) {
        productsDao.activeProduct(uid, lastModified)
    }

    override suspend fun swapProducts(
        first: Product,
        second: Product
    ): Unit = withContext(AppDispatchers.IO) {
        productsDao.updatePosition(first.productUid, first.position, first.lastModified)
        productsDao.updatePosition(second.productUid, second.position, second.lastModified)
        shoppingListsDao.updateLastModified(first.shoppingUid, first.lastModified)
    }

    override suspend fun swapProducts(products: List<Product>): Unit = withContext(AppDispatchers.IO) {
        val entities = mapping.toProductEntities(products)
        productsDao.insertProducts(entities)

        val firstProduct = products.first()
        shoppingListsDao.updateLastModified(
            uid = firstProduct.shoppingUid,
            lastModified = firstProduct.lastModified
        )
    }

    override suspend fun deleteProducts(
        uids: List<String>,
        shoppingUid: String,
        lastModified: Long
    ): Unit = withContext(AppDispatchers.IO) {
        uids.forEach { productsDao.deleteProduct(it) }
        shoppingListsDao.updateLastModified(shoppingUid, lastModified)
    }

    override suspend fun deleteShoppingListTotal(
        uid: String,
        lastModified: Long
    ): Unit = withContext(AppDispatchers.IO) {
        shoppingListsDao.deleteTotal(uid, lastModified)
    }

    override suspend fun pinProducts(
        uids: List<String>,
        lastModified: Long
    ): Unit = withContext(AppDispatchers.IO) {
        productsDao.pinProducts(uids, lastModified)
    }

    override suspend fun unpinProducts(
        uids: List<String>,
        lastModified: Long
    ): Unit = withContext(AppDispatchers.IO) {
        productsDao.unpinProducts(uids, lastModified)
    }

    override suspend fun displayAllPurchasesTotal(): Unit = withContext(AppDispatchers.IO) {
        val displayTotal = mapping.toDisplayTotalName(DisplayTotal.ALL)
        appConfigDao.displayTotal(displayTotal)
    }

    override suspend fun displayCompletedPurchasesTotal(): Unit = withContext(AppDispatchers.IO) {
        val displayTotal = mapping.toDisplayTotalName(DisplayTotal.COMPLETED)
        appConfigDao.displayTotal(displayTotal)
    }

    override suspend fun displayActivePurchasesTotal(): Unit = withContext(AppDispatchers.IO) {
        val displayTotal = mapping.toDisplayTotalName(DisplayTotal.ACTIVE)
        appConfigDao.displayTotal(displayTotal)
    }

    override suspend fun invertProductsMultiColumns(): Unit = withContext(AppDispatchers.IO) {
        appConfigDao.invertProductsMultiColumns(
            valueIfNull = !UserPreferencesDefaults.MULTI_COLUMNS
        )
    }

    override suspend fun sortProductsBy(
        shoppingUid: String,
        sortBy: SortBy,
        lastModified: Long
    ): Unit = withContext(AppDispatchers.IO) {
        val sortByName = mapping.toSortByName(sortBy)
        shoppingListsDao.sortBy(shoppingUid, sortByName, lastModified)
    }

    override suspend fun sortProductsAscending(
        shoppingUid: String,
        ascending: Boolean,
        lastModified: Long
    ): Unit = withContext(AppDispatchers.IO) {
        shoppingListsDao.sortAscending(shoppingUid, ascending, lastModified)
    }

    override suspend fun enableProductsAutomaticSorting(
        shoppingUid: String,
        sort: Sort,
        lastModified: Long
    ): Unit = withContext(AppDispatchers.IO) {
        val sortBy = mapping.toSortByName(sort.sortBy)
        shoppingListsDao.enableAutomaticSorting(shoppingUid, sortBy, sort.ascending, lastModified)
    }

    override suspend fun disableProductsAutomaticSorting(
        shoppingUid: String,
        sort: Sort,
        lastModified: Long
    ): Unit = withContext(AppDispatchers.IO) {
        val sortBy = mapping.toSortByName(sort.sortBy)
        shoppingListsDao.disableAutomaticSorting(shoppingUid, sortBy, sort.ascending, lastModified)
    }
}