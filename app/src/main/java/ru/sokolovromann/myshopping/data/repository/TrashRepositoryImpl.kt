package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import javax.inject.Inject

class TrashRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping
) : TrashRepository {

    private val shoppingListsDao = localDatasource.getShoppingListsDao()
    private val productsDao = localDatasource.getProductsDao()
    private val appConfigDao = localDatasource.getAppConfigDao()

    override suspend fun getShoppingLists(): Flow<ShoppingLists> = withContext(AppDispatchers.IO) {
        return@withContext combine(
            flow = shoppingListsDao.getTrash(),
            flow2 = shoppingListsDao.getLastPosition(),
            flow3 = appConfigDao.getAppConfig(),
            transform = { entity, lastPosition, appConfigEntity ->
                mapping.toShoppingLists(entity, lastPosition, appConfigEntity)
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

    override suspend fun deleteShoppingLists(uids: List<String>): Unit = withContext(AppDispatchers.IO) {
        shoppingListsDao.deleteShoppingsByUids(uids)
        productsDao.deleteProductsByShoppingUids(uids)
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
}