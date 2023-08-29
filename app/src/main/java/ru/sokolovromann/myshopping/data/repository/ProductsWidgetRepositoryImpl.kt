package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.Products
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import javax.inject.Inject

class ProductsWidgetRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping
) : ProductsWidgetRepository {

    private val shoppingListsDao = localDatasource.getShoppingListsDao()
    private val productsDao = localDatasource.getProductsDao()
    private val appConfigDao = localDatasource.getAppConfigDao()

    override suspend fun getShoppingLists(): Flow<ShoppingLists> = withContext(AppDispatchers.IO) {
        return@withContext shoppingListsDao.getPurchases().combine(
            flow = appConfigDao.getAppConfig(),
            transform = { entity, appConfigEntity ->
                mapping.toShoppingLists(entity, null, appConfigEntity)
            }
        )
    }

    override suspend fun getProducts(shoppingUid: String): Flow<Products?> = withContext(AppDispatchers.IO) {
        return@withContext shoppingListsDao.getShoppingList(shoppingUid).combine(
            flow = appConfigDao.getAppConfig(),
            transform = { entity, appConfigEntity ->
                if (entity == null) {
                    return@combine null
                }

                mapping.toProducts(entity, null, appConfigEntity)
            }
        )
    }

    override suspend fun completeProduct(productUid: String, lastModified: Long) = withContext(AppDispatchers.IO) {
        productsDao.completeProduct(productUid, lastModified)
    }

    override suspend fun activeProduct(productUid: String, lastModified: Long) = withContext(AppDispatchers.IO) {
        productsDao.activeProduct(productUid, lastModified)
    }
}