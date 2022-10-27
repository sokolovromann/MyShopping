package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.ArchiveDao
import ru.sokolovromann.myshopping.data.local.dao.ArchivePreferencesDao
import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import ru.sokolovromann.myshopping.data.repository.model.SortBy
import javax.inject.Inject

class ArchiveRepositoryImpl @Inject constructor(
    private val archiveDao: ArchiveDao,
    private val preferencesDao: ArchivePreferencesDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : ArchiveRepository {

    override suspend fun getShoppingLists(): Flow<ShoppingLists> = withContext(dispatchers.io) {
        return@withContext archiveDao.getShoppingLists().combine(
            flow = preferencesDao.getShoppingPreferences(),
            transform = { entity, preferencesEntity ->
                mapping.toShoppingLists(entity, preferencesEntity)
            }
        )
    }

    override suspend fun moveShoppingListToPurchases(
        uid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        archiveDao.moveShoppingToPurchases(uid, lastModified)
    }

    override suspend fun moveShoppingListToTrash(
        uid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        archiveDao.moveShoppingToTrash(uid, lastModified)
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