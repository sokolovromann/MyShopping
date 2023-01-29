package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.ProductsDao
import ru.sokolovromann.myshopping.data.local.dao.ProductsPreferencesDao
import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.Products
import ru.sokolovromann.myshopping.data.repository.model.SortBy
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(
    private val productsDao: ProductsDao,
    private val preferencesDao: ProductsPreferencesDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : ProductsRepository {

    override suspend fun getProducts(shoppingUid: String): Flow<Products?> = withContext(dispatchers.io) {
        return@withContext productsDao.getShoppingList(shoppingUid).combine(
            flow = preferencesDao.getProductsPreferences(),
            transform = { entity, preferencesEntity ->
                if (entity == null) {
                    return@combine null
                }

                mapping.toProducts(entity, preferencesEntity)
            }
        )
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

    override suspend fun sortProductsByCreated(): Unit = withContext(dispatchers.io) {
        val sortBy = mapping.toSortByName(SortBy.CREATED)
        preferencesDao.sortProductsBy(sortBy)
    }

    override suspend fun sortProductsByLastModified(): Unit = withContext(dispatchers.io) {
        val sortBy = mapping.toSortByName(SortBy.LAST_MODIFIED)
        preferencesDao.sortProductsBy(sortBy)
    }

    override suspend fun sortProductsByTotal(): Unit = withContext(dispatchers.io) {
        val sortBy = mapping.toSortByName(SortBy.TOTAL)
        preferencesDao.sortProductsBy(sortBy)
    }

    override suspend fun sortProductsByName(): Unit = withContext(dispatchers.io) {
        val sortBy = mapping.toSortByName(SortBy.NAME)
        preferencesDao.sortProductsBy(sortBy)
    }

    override suspend fun displayProductsCompletedFirst(): Unit = withContext(dispatchers.io) {
        val displayCompleted = mapping.toDisplayCompletedName(DisplayCompleted.FIRST)
        preferencesDao.displayProductsCompleted(displayCompleted)
    }

    override suspend fun displayProductsCompletedLast(): Unit = withContext(dispatchers.io) {
        val displayCompleted = mapping.toDisplayCompletedName(DisplayCompleted.LAST)
        preferencesDao.displayProductsCompleted(displayCompleted)
    }

    override suspend fun displayProductsAllTotal(): Unit = withContext(dispatchers.io) {
        val displayTotal = mapping.toDisplayTotalName(DisplayTotal.ALL)
        preferencesDao.displayProductsTotal(displayTotal)
    }

    override suspend fun displayProductsCompletedTotal(): Unit = withContext(dispatchers.io) {
        val displayTotal = mapping.toDisplayTotalName(DisplayTotal.COMPLETED)
        preferencesDao.displayProductsTotal(displayTotal)
    }

    override suspend fun displayProductsActiveTotal(): Unit = withContext(dispatchers.io) {
        val displayTotal = mapping.toDisplayTotalName(DisplayTotal.ACTIVE)
        preferencesDao.displayProductsTotal(displayTotal)
    }

    override suspend fun invertProductsSort(): Unit = withContext(dispatchers.io) {
        preferencesDao.invertProductsSort()
    }

    override suspend fun hideProductsCompleted(): Unit = withContext(dispatchers.io) {
        val displayCompleted = mapping.toDisplayCompletedName(DisplayCompleted.HIDE)
        preferencesDao.displayProductsCompleted(displayCompleted)
    }
}