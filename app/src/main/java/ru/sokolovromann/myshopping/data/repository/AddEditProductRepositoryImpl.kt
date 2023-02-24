package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.AddEditProductDao
import ru.sokolovromann.myshopping.data.local.dao.AddEditProductPreferencesDao
import ru.sokolovromann.myshopping.data.local.resources.AddEditProductsResources
import ru.sokolovromann.myshopping.data.repository.model.*
import javax.inject.Inject

class AddEditProductRepositoryImpl @Inject constructor(
    private val productDao: AddEditProductDao,
    private val resources: AddEditProductsResources,
    private val preferencesDao: AddEditProductPreferencesDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : AddEditProductRepository {

    override suspend fun getProducts(
        search: String
    ): Flow<AddEditProductProducts> = withContext(dispatchers.io) {
        return@withContext productDao.getProducts(search).combine(
            flow = preferencesDao.getProductPreferences(),
            transform = { entities, preferencesEntity ->
                mapping.toAddEditProductProducts(entities, preferencesEntity)
            }
        )
    }

    override suspend fun getAddEditProduct(
        shoppingUid: String,
        productUid: String?
    ): Flow<AddEditProduct> = withContext(dispatchers.io) {
        return@withContext if (productUid == null) {
            productDao.getProductsLastPosition(shoppingUid).combine(
                flow = preferencesDao.getProductPreferences(),
                transform = { lastPosition, preferencesEntity ->
                    mapping.toAddEditProduct(null, lastPosition, preferencesEntity)
                }
            )
        } else {
            combine(
                flow = productDao.getProduct(productUid),
                flow2 = productDao.getProductsLastPosition(shoppingUid),
                flow3 = preferencesDao.getProductPreferences(),
                transform = { entity, lastPosition, preferencesEntity ->
                    mapping.toAddEditProduct(entity, lastPosition, preferencesEntity)
                }
            )
        }
    }

    override suspend fun getAutocompletes(
        search: String
    ): Flow<AddEditProductAutocompletes> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = productDao.getAutocompletes(search),
            flow2 = resources.getDefaultAutocompleteNames(search),
            flow3 = preferencesDao.getProductPreferences(),
            transform = { entities, resources, preferencesEntity ->
                mapping.toAddEditProductAutocompletes(entities, resources, preferencesEntity)
            }
        )
    }

    override suspend fun addProduct(product: Product): Unit = withContext(dispatchers.io) {
        val entity = mapping.toProductEntity(product)
        productDao.insertProduct(entity)
        productDao.updateShoppingLastModified(entity.shoppingUid, entity.lastModified)
    }

    override suspend fun editProduct(product: Product): Unit = withContext(dispatchers.io) {
        val entity = mapping.toProductEntity(product)
        productDao.insertProduct(entity)
        productDao.updateShoppingLastModified(entity.shoppingUid, entity.lastModified)
    }

    override suspend fun addAutocomplete(autocomplete: Autocomplete): Unit = withContext(dispatchers.io) {
        val entity = mapping.toAutocompleteEntity(autocomplete)
        productDao.insertAutocomplete(entity)
    }

    override suspend fun saveProductLock(productLock: ProductLock): Unit = withContext(dispatchers.io) {
        val value = mapping.toProductLockName(productLock)
        preferencesDao.saveProductsProductLock(value)
    }
}