package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.AppConfigDao
import ru.sokolovromann.myshopping.data.local.dao.MoveProductDao
import ru.sokolovromann.myshopping.data.repository.model.Product
import ru.sokolovromann.myshopping.data.repository.model.ShoppingList
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import javax.inject.Inject

class MoveProductRepositoryImpl @Inject constructor(
    private val productDao: MoveProductDao,
    private val appConfigDao: AppConfigDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : MoveProductRepository {

    override suspend fun getPurchases(): Flow<ShoppingLists> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = productDao.getPurchases(),
            flow2 = productDao.getShoppingsLastPosition(),
            flow3 = appConfigDao.getAppConfig(),
            transform = { entity, lastPosition, appConfigEntity ->
                mapping.toShoppingLists(entity, lastPosition, appConfigEntity)
            }
        )
    }

    override suspend fun getArchive(): Flow<ShoppingLists> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = productDao.getArchive(),
            flow2 = productDao.getShoppingsLastPosition(),
            flow3 = appConfigDao.getAppConfig(),
            transform = { entity, lastPosition, appConfigEntity ->
                mapping.toShoppingLists(entity, lastPosition, appConfigEntity)
            }
        )
    }

    override suspend fun getProducts(
        uids: List<String>
    ): Flow<List<Product>> = withContext(dispatchers.io) {
        return@withContext productDao.getProducts(uids).combine(
            flow = appConfigDao.getAppConfig(),
            transform = { entities, appConfigEntity ->
                mapping.toProducts(entities, appConfigEntity)
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

    override suspend fun editProducts(
        products: List<Product>
    ): Unit = withContext(dispatchers.io) {
        val entities = mapping.toProductEntities(products)
        productDao.insertProducts(entities)
        productDao.updateShoppingLastModified(
            entities.first().shoppingUid,
            entities.first().lastModified
        )
    }
}