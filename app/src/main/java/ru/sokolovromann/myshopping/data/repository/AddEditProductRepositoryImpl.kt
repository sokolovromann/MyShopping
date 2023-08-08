package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.AddEditProductDao
import ru.sokolovromann.myshopping.data.local.dao.AppConfigDao
import ru.sokolovromann.myshopping.data.local.resources.AddEditProductsResources
import ru.sokolovromann.myshopping.data.repository.model.*
import javax.inject.Inject

class AddEditProductRepositoryImpl @Inject constructor(
    private val productDao: AddEditProductDao,
    private val resources: AddEditProductsResources,
    private val appConfigDao: AppConfigDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : AddEditProductRepository {

    override suspend fun getAddEditProduct(
        shoppingUid: String,
        productUid: String?
    ): Flow<AddEditProduct> = withContext(dispatchers.io) {
        return@withContext if (productUid == null) {
            productDao.getProductsLastPosition(shoppingUid).combine(
                flow = appConfigDao.getAppConfig(),
                transform = { lastPosition, appConfigEntity ->
                    mapping.toAddEditProduct(null, lastPosition, appConfigEntity)
                }
            )
        } else {
            combine(
                flow = productDao.getProduct(productUid),
                flow2 = productDao.getProductsLastPosition(shoppingUid),
                flow3 = appConfigDao.getAppConfig(),
                transform = { entity, lastPosition, appConfigEntity ->
                    mapping.toAddEditProduct(entity, lastPosition, appConfigEntity)
                }
            )
        }
    }

    override suspend fun getAutocompletes(
        search: String,
        language: String
    ): Flow<Autocompletes> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = productDao.getAutocompletes(search),
            flow2 = resources.getDefaultAutocompleteNames(search),
            flow3 = appConfigDao.getAppConfig(),
            transform = { entities, resources, appConfigEntity ->
                mapping.toAutocompletes(entities, resources, appConfigEntity, language)
            }
        )
    }

    override suspend fun checkIfProductUidExists(uid: String): Flow<String?> = withContext(dispatchers.io) {
        return@withContext productDao.checkIfProductUidExists(uid)
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

    override suspend fun lockProductQuantity(): Unit = withContext(dispatchers.io) {
        val value = mapping.toLockProductElementName(LockProductElement.QUANTITY)
        appConfigDao.lockProductElement(value)
    }

    override suspend fun lockProductPrice(): Unit = withContext(dispatchers.io) {
        val value = mapping.toLockProductElementName(LockProductElement.PRICE)
        appConfigDao.lockProductElement(value)
    }

    override suspend fun lockProductTotal(): Unit = withContext(dispatchers.io) {
        val value = mapping.toLockProductElementName(LockProductElement.TOTAL)
        appConfigDao.lockProductElement(value)
    }
}