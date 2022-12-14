package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.MoveProductDao
import ru.sokolovromann.myshopping.data.local.dao.MoveProductPreferencesDao
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import javax.inject.Inject

class MoveProductRepositoryImpl @Inject constructor(
    private val productDao: MoveProductDao,
    private val preferencesDao: MoveProductPreferencesDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : MoveProductRepository {

    override suspend fun getPurchases(): Flow<ShoppingLists> = withContext(dispatchers.io) {
        return@withContext productDao.getPurchases().combine(
            flow = preferencesDao.getShoppingPreferences(),
            transform = { entity, preferencesEntity ->
                mapping.toShoppingLists(entity, preferencesEntity)
            }
        )
    }

    override suspend fun getArchive(): Flow<ShoppingLists> = withContext(dispatchers.io) {
        return@withContext productDao.getArchive().combine(
            flow = preferencesDao.getShoppingPreferences(),
            transform = { entity, preferencesEntity ->
                mapping.toShoppingLists(entity, preferencesEntity)
            }
        )
    }

    override suspend fun getTrash(): Flow<ShoppingLists> = withContext(dispatchers.io) {
        return@withContext productDao.getTrash().combine(
            flow = preferencesDao.getShoppingPreferences(),
            transform = { entity, preferencesEntity ->
                mapping.toShoppingLists(entity, preferencesEntity)
            }
        )
    }

    override suspend fun moveProduct(
        productUid: String,
        shoppingUid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        productDao.moveProduct(productUid, shoppingUid, lastModified)
    }
}