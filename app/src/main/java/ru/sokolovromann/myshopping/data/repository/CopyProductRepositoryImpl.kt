package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.CopyProductDao
import ru.sokolovromann.myshopping.data.local.dao.CopyProductPreferencesDao
import ru.sokolovromann.myshopping.data.repository.model.Product
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import javax.inject.Inject

class CopyProductRepositoryImpl @Inject constructor(
    private val productDao: CopyProductDao,
    private val preferencesDao: CopyProductPreferencesDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : CopyProductRepository {

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

    override suspend fun getProduct(uid: String): Flow<Product?> = withContext(dispatchers.io) {
        return@withContext productDao.getProduct(uid).combine(
            flow = preferencesDao.getShoppingPreferences(),
            transform = { entity, preferencesEntity ->
                if (entity == null) {
                    return@combine null
                }

                mapping.toProduct(entity, preferencesEntity)
            }
        )
    }

    override suspend fun addProduct(product: Product): Unit = withContext(dispatchers.io) {
        val entity = mapping.toProductEntity(product)
        productDao.insertProduct(entity)
        productDao.updateShoppingLastModified(entity.shoppingUid, entity.lastModified)
    }
}