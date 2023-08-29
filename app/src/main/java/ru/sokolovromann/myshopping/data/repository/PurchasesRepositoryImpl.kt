package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.*
import javax.inject.Inject

class PurchasesRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping
) : PurchasesRepository {

    private val shoppingListsDao = localDatasource.getShoppingListsDao()
    private val productsDao = localDatasource.getProductsDao()
    private val appConfigDao = localDatasource.getAppConfigDao()

    override suspend fun getShoppingLists(): Flow<ShoppingLists> = withContext(AppDispatchers.IO) {
        return@withContext combine(
            flow = shoppingListsDao.getPurchases(),
            flow2 = shoppingListsDao.getLastPosition(),
            flow3 = appConfigDao.getAppConfig(),
            transform = { entity, lastPosition, appConfigEntity ->
                mapping.toShoppingLists(entity, lastPosition, appConfigEntity)
            }
        )
    }

    override suspend fun getShoppingListsLastPosition(): Flow<Int?> = withContext(AppDispatchers.IO) {
        return@withContext shoppingListsDao.getLastPosition()
    }

    override suspend fun addShoppingList(
        shoppingList: ShoppingList
    ): Unit = withContext(AppDispatchers.IO) {
        val shoppingEntity = mapping.toShoppingEntity(shoppingList)
        shoppingListsDao.insertShopping(shoppingEntity)
    }

    override suspend fun moveShoppingListsToArchive(
        uids: List<String>,
        lastModified: Long
    ): Unit = withContext(AppDispatchers.IO) {
        uids.forEach {
            shoppingListsDao.moveToArchive(it, lastModified)
        }
    }

    override suspend fun moveShoppingListsToTrash(
        uids: List<String>,
        lastModified: Long
    ): Unit = withContext(AppDispatchers.IO) {
        uids.forEach {
            shoppingListsDao.moveToTrash(it, lastModified)
        }
    }

    override suspend fun copyShoppingLists(
        shoppingLists: List<ShoppingList>
    ): Unit = withContext(AppDispatchers.IO) {
        shoppingLists.forEach { shoppingList ->
            val shoppingEntity = mapping.toShoppingEntity(shoppingList)
            shoppingListsDao.insertShopping(shoppingEntity)

            shoppingList.products.forEach { product ->
                val productEntity = mapping.toProductEntity(product)
                productsDao.insertProduct(productEntity)
            }
        }
    }

    override suspend fun swapShoppingLists(
        first: ShoppingList,
        second: ShoppingList
    ): Unit = withContext(AppDispatchers.IO) {
        shoppingListsDao.updatePosition(first.uid, first.position, first.lastModified)
        shoppingListsDao.updatePosition(second.uid, second.position, second.lastModified)
    }

    override suspend fun swapShoppingLists(
        shoppingLists: List<ShoppingList>
    ): Unit = withContext(AppDispatchers.IO) {
        val entities = mapping.toShoppingEntities(shoppingLists)
        shoppingListsDao.insertShoppings(entities)
    }

    override suspend fun pinShoppingLists(
        uids: List<String>,
        lastModified: Long
    ): Unit = withContext(AppDispatchers.IO) {
        shoppingListsDao.pinShoppings(uids, lastModified)
    }

    override suspend fun unpinShoppingLists(
        uids: List<String>,
        lastModified: Long
    ): Unit = withContext(AppDispatchers.IO) {
        shoppingListsDao.unpinShoppings(uids, lastModified)
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

    override suspend fun invertShoppingListsMultiColumns(): Unit = withContext(AppDispatchers.IO) {
        appConfigDao.invertShoppingsMultiColumns(
            valueIfNull = !UserPreferencesDefaults.MULTI_COLUMNS
        )
    }
}