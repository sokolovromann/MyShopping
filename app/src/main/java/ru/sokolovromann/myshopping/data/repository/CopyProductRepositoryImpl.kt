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
            flow = preferencesDao.getAppPreferences(),
            transform = { entity, preferencesEntity ->
                mapping.toShoppingLists(entity, null, preferencesEntity)
            }
        )
    }

    override suspend fun getArchive(): Flow<ShoppingLists> = withContext(dispatchers.io) {
        return@withContext productDao.getArchive().combine(
            flow = preferencesDao.getAppPreferences(),
            transform = { entity, preferencesEntity ->
                mapping.toShoppingLists(entity, null, preferencesEntity)
            }
        )
    }

    override suspend fun getProducts(
        uids: List<String>
    ): Flow<List<Product>> = withContext(dispatchers.io) {
        return@withContext productDao.getProducts(uids).combine(
            flow = preferencesDao.getAppPreferences(),
            transform = { entities, preferencesEntity ->
                mapping.toProducts(entities, preferencesEntity)
            }
        )
    }

    override suspend fun addProducts(products: List<Product>): Unit = withContext(dispatchers.io) {
        val entities = mapping.toProductEntities(products)
        productDao.insertProducts(entities)
        productDao.updateShoppingLastModified(
            entities.first().shoppingUid,
            entities.first().lastModified
        )
    }
}