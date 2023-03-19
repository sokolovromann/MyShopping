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
        return@withContext productsDao.getShoppingList(shoppingUid).combine(
            flow = preferencesDao.getAppPreferences(),
            transform = { entity, preferencesEntity ->
                if (entity == null) {
                    return@combine null
                }

                mapping.toProducts(entity, preferencesEntity)
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

    override suspend fun hideProducts(
        shoppingUid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        productsDao.hideProducts(shoppingUid)
        productsDao.updateShoppingLastModified(shoppingUid, lastModified)
    }

    override suspend fun hideProduct(
        shoppingUid: String,
        productUid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        productsDao.hideProduct(productUid)
        productsDao.updateShoppingLastModified(shoppingUid, lastModified)
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
}