package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.ShoppingList
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import ru.sokolovromann.myshopping.data.repository.model.UserPreferencesDefaults
import javax.inject.Inject

class ArchiveRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : ArchiveRepository {

    private val shoppingListsDao = localDatasource.getShoppingListsDao()
    private val appConfigDao = localDatasource.getAppConfigDao()

    override suspend fun getShoppingLists(): Flow<ShoppingLists> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = shoppingListsDao.getArchive(),
            flow2 = shoppingListsDao.getLastPosition(),
            flow3 = appConfigDao.getAppConfig(),
            transform = { entity, lastPosition, appConfigEntity ->
                mapping.toShoppingLists(entity, lastPosition, appConfigEntity)
            }
        )
    }

    override suspend fun moveShoppingListsToPurchases(
        uids: List<String>,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        uids.forEach {
            shoppingListsDao.moveToPurchases(it, lastModified)
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

    override suspend fun swapShoppingLists(
        shoppingLists: List<ShoppingList>
    ): Unit = withContext(dispatchers.io) {
        val entities = mapping.toShoppingEntities(shoppingLists)
        shoppingListsDao.insertShoppings(entities)
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