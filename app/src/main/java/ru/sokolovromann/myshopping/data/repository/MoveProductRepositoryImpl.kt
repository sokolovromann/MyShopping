package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.Product
import ru.sokolovromann.myshopping.data.repository.model.ShoppingList
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import javax.inject.Inject

class MoveProductRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : MoveProductRepository {

    private val shoppingListsDao = localDatasource.getShoppingListsDao()
    private val productsDao = localDatasource.getProductsDao()
    private val appConfigDao = localDatasource.getAppConfigDao()

    override suspend fun getPurchases(): Flow<ShoppingLists> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = shoppingListsDao.getPurchases(),
            flow2 = shoppingListsDao.getLastPosition(),
            flow3 = appConfigDao.getAppConfig(),
            transform = { entity, lastPosition, appConfigEntity ->
                mapping.toShoppingLists(entity, lastPosition, appConfigEntity)
            }
        )
    }

    override suspend fun getArchive(): Flow<ShoppingLists> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = shoppingListsDao.getArchive(),
            flow2 = shoppingListsDao.getLastPosition(),
            flow3 = appConfigDao.getAppConfig(),
            transform = { entity, lastPosition, appConfigEntity ->
                mapping.toShoppingLists(entity, lastPosition, appConfigEntity)
            }
        )
    }

    override suspend fun getProducts(
        uids: List<String>
    ): Flow<List<Product>> = withContext(dispatchers.io) {
        return@withContext productsDao.getProducts(uids).combine(
            flow = appConfigDao.getAppConfig(),
            transform = { entities, appConfigEntity ->
                mapping.toProducts(entities, appConfigEntity)
            }
        )
    }

    override suspend fun getShoppingListsLastPosition(): Flow<Int?> = withContext(dispatchers.io) {
        return@withContext shoppingListsDao.getLastPosition()
    }

    override suspend fun addShoppingList(shoppingList: ShoppingList): Unit = withContext(dispatchers.io) {
        val entity = mapping.toShoppingEntity(shoppingList)
        shoppingListsDao.insertShopping(entity)
    }

    override suspend fun editProducts(
        products: List<Product>
    ): Unit = withContext(dispatchers.io) {
        val entities = mapping.toProductEntities(products)
        productsDao.insertProducts(entities)
        shoppingListsDao.updateLastModified(
            entities.first().shoppingUid,
            entities.first().lastModified
        )
    }
}