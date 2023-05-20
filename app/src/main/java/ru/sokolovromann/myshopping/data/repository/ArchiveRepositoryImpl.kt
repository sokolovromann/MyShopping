package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.ArchiveDao
import ru.sokolovromann.myshopping.data.local.dao.ArchivePreferencesDao
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.ShoppingList
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import javax.inject.Inject

class ArchiveRepositoryImpl @Inject constructor(
    private val archiveDao: ArchiveDao,
    private val preferencesDao: ArchivePreferencesDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : ArchiveRepository {

    override suspend fun getShoppingLists(): Flow<ShoppingLists> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = archiveDao.getShoppingLists(),
            flow2 = archiveDao.getShoppingsLastPosition(),
            flow3 = preferencesDao.getAppPreferences(),
            transform = { entity, lastPosition, preferencesEntity ->
                mapping.toShoppingLists(entity, lastPosition, preferencesEntity)
            }
        )
    }

    override suspend fun moveShoppingListsToPurchases(
        uids: List<String>,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        uids.forEach {
            archiveDao.moveShoppingToPurchases(it, lastModified)
        }
    }

    override suspend fun moveShoppingListsToTrash(
        uids: List<String>,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        uids.forEach {
            archiveDao.moveShoppingToTrash(it, lastModified)
        }
    }

    override suspend fun swapShoppingLists(
        shoppingLists: List<ShoppingList>
    ): Unit = withContext(dispatchers.io) {
        val entities = mapping.toShoppingEntities(shoppingLists)
        archiveDao.updateShoppings(entities)
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

    override suspend fun invertShoppingListsMultiColumns(): Unit = withContext(dispatchers.io) {
        preferencesDao.invertShoppingsMultiColumns()
    }
}