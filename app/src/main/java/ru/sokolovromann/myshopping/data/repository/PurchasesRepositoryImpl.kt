package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.*
import javax.inject.Inject

class PurchasesRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : PurchasesRepository {

    private val shoppingListsDao = localDatasource.getShoppingListsDao()
    private val productsDao = localDatasource.getProductsDao()
    private val appConfigDao = localDatasource.getAppConfigDao()

    override suspend fun getShoppingLists(): Flow<ShoppingLists> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = shoppingListsDao.getPurchases(),
            flow2 = shoppingListsDao.getLastPosition(),
            flow3 = appConfigDao.getAppConfig(),
            transform = { entity, lastPosition, appConfigEntity ->
                mapping.toShoppingLists(entity, lastPosition, appConfigEntity)
            }
        )
    }

    override suspend fun getShoppingListsLastPosition(): Flow<Int?> = withContext(dispatchers.io) {
        return@withContext shoppingListsDao.getLastPosition()
    }

    override suspend fun addShoppingList(
        shoppingList: ShoppingList
    ): Unit = withContext(dispatchers.io) {
        val shoppingEntity = mapping.toShoppingEntity(shoppingList)
        shoppingListsDao.insertShopping(shoppingEntity)
    }

    override suspend fun moveShoppingListsToArchive(
        uids: List<String>,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        uids.forEach {
            shoppingListsDao.moveToArchive(it, lastModified)
        }
    }

    override suspend fun moveShoppingListsToTrash(
        uids: List<String>,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        uids.forEach {
            shoppingListsDao.moveToTrash(it, lastModified)
        }
    }

    override suspend fun copyShoppingLists(
        shoppingLists: List<ShoppingList>
    ): Unit = withContext(dispatchers.io) {
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
    ): Unit = withContext(dispatchers.io) {
        shoppingListsDao.updatePosition(first.uid, first.position, first.lastModified)
        shoppingListsDao.updatePosition(second.uid, second.position, second.lastModified)
    }

    override suspend fun swapShoppingLists(
        shoppingLists: List<ShoppingList>
    ): Unit = withContext(dispatchers.io) {
        val entities = mapping.toShoppingEntities(shoppingLists)
        shoppingListsDao.insertShoppings(entities)
    }

    override suspend fun pinShoppingLists(
        uids: List<String>,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        shoppingListsDao.pinShoppings(uids, lastModified)
    }

    override suspend fun unpinShoppingLists(
        uids: List<String>,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        shoppingListsDao.unpinShoppings(uids, lastModified)
    }

    override suspend fun displayAllPurchasesTotal(): Unit = withContext(dispatchers.io) {
        val displayTotal = mapping.toDisplayTotalName(DisplayTotal.ALL)
        appConfigDao.displayTotal(displayTotal)
    }

    override suspend fun displayCompletedPurchasesTotal(): Unit = withContext(dispatchers.io) {
        val displayTotal = mapping.toDisplayTotalName(DisplayTotal.COMPLETED)
        appConfigDao.displayTotal(displayTotal)
    }

    override suspend fun displayActivePurchasesTotal(): Unit = withContext(dispatchers.io) {
        val displayTotal = mapping.toDisplayTotalName(DisplayTotal.ACTIVE)
        appConfigDao.displayTotal(displayTotal)
    }

    override suspend fun invertShoppingListsMultiColumns(): Unit = withContext(dispatchers.io) {
        appConfigDao.invertShoppingsMultiColumns(
            valueIfNull = !UserPreferencesDefaults.MULTI_COLUMNS
        )
    }
}