package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.ProductsWidgetDao
import ru.sokolovromann.myshopping.data.local.dao.ProductsWidgetPreferencesDao
import ru.sokolovromann.myshopping.data.repository.model.Products
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import javax.inject.Inject

class ProductsWidgetRepositoryImpl @Inject constructor(
    private val widgetDao: ProductsWidgetDao,
    private val preferencesDao: ProductsWidgetPreferencesDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : ProductsWidgetRepository {

    override suspend fun getShoppingLists(): Flow<ShoppingLists> = withContext(dispatchers.io) {
        return@withContext widgetDao.getShoppingLists().combine(
            flow = preferencesDao.getAppPreferences(),
            transform = { entity, preferencesEntity ->
                mapping.toShoppingLists(entity, null, preferencesEntity)
            }
        )
    }

    override suspend fun getProducts(shoppingUid: String): Flow<Products?> = withContext(dispatchers.io) {
        return@withContext widgetDao.getShoppingList(shoppingUid).combine(
            flow = preferencesDao.getAppPreferences(),
            transform = { entity, preferencesEntity ->
                if (entity == null) {
                    return@combine null
                }

                mapping.toProducts(entity, preferencesEntity)
            }
        )
    }

    override suspend fun completeProduct(productUid: String, lastModified: Long) = withContext(dispatchers.io) {
        widgetDao.completeProduct(productUid, lastModified)
    }

    override suspend fun activeProduct(productUid: String, lastModified: Long) = withContext(dispatchers.io) {
        widgetDao.activeProduct(productUid, lastModified)
    }
}