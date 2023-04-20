package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.TrashDao
import ru.sokolovromann.myshopping.data.local.dao.TrashPreferencesDao
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import javax.inject.Inject

class TrashRepositoryImpl @Inject constructor(
    private val trashDao: TrashDao,
    private val preferencesDao: TrashPreferencesDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : TrashRepository {

    override suspend fun getShoppingLists(): Flow<ShoppingLists> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = trashDao.getShoppingLists(),
            flow2 = trashDao.getShoppingsLastPosition(),
            flow3 = preferencesDao.getAppPreferences(),
            transform = { entity, lastPosition, preferencesEntity ->
                mapping.toShoppingLists(entity, lastPosition, preferencesEntity)
            }
        )
    }

    override suspend fun moveShoppingListToPurchases(
        uid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        trashDao.moveShoppingToPurchases(uid, lastModified)
    }

    override suspend fun moveShoppingListToArchive(
        uid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        trashDao.moveShoppingToArchive(uid, lastModified)
    }

    override suspend fun deleteShoppingLists(uids: List<String>): Unit = withContext(dispatchers.io) {
        uids.forEach {
            trashDao.deleteShoppingList(it)
            trashDao.deleteProducts(it)
        }
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