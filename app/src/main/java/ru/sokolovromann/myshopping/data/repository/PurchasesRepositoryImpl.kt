package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.PurchasesDao
import ru.sokolovromann.myshopping.data.local.dao.PurchasesPreferencesDao
import ru.sokolovromann.myshopping.data.repository.model.*
import javax.inject.Inject

class PurchasesRepositoryImpl @Inject constructor(
    private val purchasesDao: PurchasesDao,
    private val preferencesDao: PurchasesPreferencesDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : PurchasesRepository {

    override suspend fun getShoppingLists(): Flow<ShoppingLists> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = purchasesDao.getShoppingLists(),
            flow2 = purchasesDao.getShoppingsLastPosition(),
            flow3 = preferencesDao.getShoppingPreferences(),
            transform = { entity, lastPosition, preferencesEntity ->
                mapping.toShoppingLists(entity, lastPosition, preferencesEntity)
            }
        )
    }

    override suspend fun addShoppingList(
        shoppingList: ShoppingList
    ): Unit = withContext(dispatchers.io) {
        val shoppingEntity = mapping.toShoppingEntity(shoppingList)
        purchasesDao.insertShopping(shoppingEntity)
    }

    override suspend fun moveShoppingListToArchive(
        uid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        purchasesDao.moveShoppingToArchive(uid, lastModified)
    }

    override suspend fun moveShoppingListToTrash(
        uid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        purchasesDao.moveShoppingToTrash(uid, lastModified)
    }

    override suspend fun swapShoppingLists(
        first: ShoppingList,
        second: ShoppingList
    ): Unit = withContext(dispatchers.io) {
        purchasesDao.updateShoppingPosition(first.uid, first.position, first.lastModified)
        purchasesDao.updateShoppingPosition(second.uid, second.position, second.lastModified)
    }

    override suspend fun sortShoppingListsByCreated(): Unit = withContext(dispatchers.io) {
        val sortBy = mapping.toSortByName(SortBy.CREATED)
        preferencesDao.sortShoppingsBy(sortBy)
    }

    override suspend fun sortShoppingListsByLastModified(): Unit = withContext(dispatchers.io) {
        val sortBy = mapping.toSortByName(SortBy.LAST_MODIFIED)
        preferencesDao.sortShoppingsBy(sortBy)
    }

    override suspend fun sortShoppingListsByName(): Unit = withContext(dispatchers.io) {
        val sortBy = mapping.toSortByName(SortBy.NAME)
        preferencesDao.sortShoppingsBy(sortBy)
    }

    override suspend fun sortShoppingListsByTotal(): Unit = withContext(dispatchers.io) {
        val sortBy = mapping.toSortByName(SortBy.TOTAL)
        preferencesDao.sortShoppingsBy(sortBy)
    }

    override suspend fun displayShoppingListsCompletedFirst(): Unit = withContext(dispatchers.io) {
        val displayCompleted = mapping.toDisplayCompletedName(DisplayCompleted.FIRST)
        preferencesDao.displayShoppingsCompleted(displayCompleted)
    }

    override suspend fun displayShoppingListsCompletedLast(): Unit = withContext(dispatchers.io) {
        val displayCompleted = mapping.toDisplayCompletedName(DisplayCompleted.LAST)
        preferencesDao.displayShoppingsCompleted(displayCompleted)
    }

    override suspend fun displayShoppingListsAllTotal(): Unit = withContext(dispatchers.io) {
        val displayTotal = mapping.toDisplayTotalName(DisplayTotal.ALL)
        preferencesDao.displayShoppingsTotal(displayTotal)
    }

    override suspend fun displayShoppingListsCompletedTotal(): Unit = withContext(dispatchers.io) {
        val displayTotal = mapping.toDisplayTotalName(DisplayTotal.COMPLETED)
        preferencesDao.displayShoppingsTotal(displayTotal)
    }

    override suspend fun displayShoppingListsActiveTotal(): Unit = withContext(dispatchers.io) {
        val displayTotal = mapping.toDisplayTotalName(DisplayTotal.ACTIVE)
        preferencesDao.displayShoppingsTotal(displayTotal)
    }

    override suspend fun invertShoppingListsSort(): Unit = withContext(dispatchers.io) {
        preferencesDao.invertShoppingsSort()
    }

    override suspend fun hideShoppingListsCompleted(): Unit = withContext(dispatchers.io) {
        val displayCompleted = mapping.toDisplayCompletedName(DisplayCompleted.HIDE)
        preferencesDao.displayShoppingsCompleted(displayCompleted)
    }
}