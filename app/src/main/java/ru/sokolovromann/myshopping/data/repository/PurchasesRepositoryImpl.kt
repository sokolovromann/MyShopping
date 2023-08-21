package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.AppConfigDao
import ru.sokolovromann.myshopping.data.local.dao.PurchasesDao
import ru.sokolovromann.myshopping.data.repository.model.*
import javax.inject.Inject

class PurchasesRepositoryImpl @Inject constructor(
    private val purchasesDao: PurchasesDao,
    private val appConfigDao: AppConfigDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : PurchasesRepository {

    override suspend fun getShoppingLists(): Flow<ShoppingLists> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = purchasesDao.getShoppingLists(),
            flow2 = purchasesDao.getShoppingsLastPosition(),
            flow3 = appConfigDao.getAppConfig(),
            transform = { entity, lastPosition, appConfigEntity ->
                mapping.toShoppingLists(entity, lastPosition, appConfigEntity)
            }
        )
    }

    override suspend fun getShoppingListsLastPosition(): Flow<Int?> = withContext(dispatchers.io) {
        return@withContext purchasesDao.getShoppingsLastPosition()
    }

    override suspend fun addShoppingList(
        shoppingList: ShoppingList
    ): Unit = withContext(dispatchers.io) {
        val shoppingEntity = mapping.toShoppingEntity(shoppingList)
        purchasesDao.insertShopping(shoppingEntity)
    }

    override suspend fun moveShoppingListsToArchive(
        uids: List<String>,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        uids.forEach {
            purchasesDao.moveShoppingToArchive(it, lastModified)
        }
    }

    override suspend fun moveShoppingListsToTrash(
        uids: List<String>,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        uids.forEach {
            purchasesDao.moveShoppingToTrash(it, lastModified)
        }
    }

    override suspend fun copyShoppingLists(
        shoppingLists: List<ShoppingList>
    ): Unit = withContext(dispatchers.io) {
        shoppingLists.forEach { shoppingList ->
            val shoppingEntity = mapping.toShoppingEntity(shoppingList)
            purchasesDao.insertShopping(shoppingEntity)

            shoppingList.products.forEach { product ->
                val productEntity = mapping.toProductEntity(product)
                purchasesDao.insertProduct(productEntity)
            }
        }
    }

    override suspend fun swapShoppingLists(
        first: ShoppingList,
        second: ShoppingList
    ): Unit = withContext(dispatchers.io) {
        purchasesDao.updateShoppingPosition(first.uid, first.position, first.lastModified)
        purchasesDao.updateShoppingPosition(second.uid, second.position, second.lastModified)
    }

    override suspend fun swapShoppingLists(
        shoppingLists: List<ShoppingList>
    ): Unit = withContext(dispatchers.io) {
        val entities = mapping.toShoppingEntities(shoppingLists)
        purchasesDao.updateShoppings(entities)
    }

    override suspend fun pinShoppingLists(
        uids: List<String>,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        uids.forEach { purchasesDao.pinShopping(it, lastModified) }
    }

    override suspend fun unpinShoppingLists(
        uids: List<String>,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        uids.forEach { purchasesDao.unpinShopping(it, lastModified) }
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