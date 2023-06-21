package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.CopyProductDao
import ru.sokolovromann.myshopping.data.local.dao.CopyProductPreferencesDao
import ru.sokolovromann.myshopping.data.repository.model.Product
import ru.sokolovromann.myshopping.data.repository.model.ShoppingList
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import javax.inject.Inject

class CopyProductRepositoryImpl @Inject constructor(
    private val productDao: CopyProductDao,
    private val preferencesDao: CopyProductPreferencesDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : CopyProductRepository {

    override suspend fun getPurchases(): Flow<ShoppingLists> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = productDao.getPurchases(),
            flow2 = productDao.getShoppingsLastPosition(),
            flow3 = preferencesDao.getAppPreferences(),
            transform = { entity, lastPosition, preferencesEntity ->
                mapping.toShoppingLists(entity, lastPosition, preferencesEntity)
            }
        )
    }

    override suspend fun getArchive(): Flow<ShoppingLists> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = productDao.getArchive(),
            flow2 = productDao.getShoppingsLastPosition(),
            flow3 = preferencesDao.getAppPreferences(),
            transform = { entity, lastPosition, preferencesEntity ->
                mapping.toShoppingLists(entity, lastPosition, preferencesEntity)
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

    override suspend fun getShoppingListsLastPosition(): Flow<Int?> = withContext(dispatchers.io) {
        return@withContext productDao.getShoppingsLastPosition()
    }

    override suspend fun addShoppingList(shoppingList: ShoppingList): Unit = withContext(dispatchers.io) {
        val entity = mapping.toShoppingEntity(shoppingList)
        productDao.insertShopping(entity)
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