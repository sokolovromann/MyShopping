package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.*
import javax.inject.Inject

class AddEditProductRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping
) : AddEditProductRepository {

    private val shoppingListsDao = localDatasource.getShoppingListsDao()
    private val productDao = localDatasource.getProductsDao()
    private val autocompletesDao = localDatasource.getAutocompletesDao()
    private val appConfigDao = localDatasource.getAppConfigDao()
    private val resourcesDao = localDatasource.getResourcesDao()

    override suspend fun getAddEditProduct(
        shoppingUid: String,
        productUid: String?
    ): Flow<AddEditProduct> = withContext(AppDispatchers.IO) {
        return@withContext if (productUid == null) {
            productDao.getLastPosition(shoppingUid).combine(
                flow = appConfigDao.getAppConfig(),
                transform = { lastPosition, appConfigEntity ->
                    mapping.toAddEditProduct(null, lastPosition, appConfigEntity)
                }
            )
        } else {
            combine(
                flow = productDao.getProduct(productUid),
                flow2 = productDao.getLastPosition(shoppingUid),
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
    ): Flow<Autocompletes> = withContext(AppDispatchers.IO) {
        return@withContext combine(
            flow = autocompletesDao.searchAutocompletesLikeName(search),
            flow2 = appConfigDao.getAppConfig(),
            transform = { entities, appConfigEntity ->
                val resources = resourcesDao.searchAutocompleteNames(search)
                mapping.toAutocompletes(entities, resources, appConfigEntity, language)
            }
        )
    }

    override suspend fun checkIfProductUidExists(uid: String): Flow<String?> = withContext(AppDispatchers.IO) {
        return@withContext productDao.checkIfProductExists(uid)
    }

    override suspend fun addProduct(product: Product): Unit = withContext(AppDispatchers.IO) {
        val entity = mapping.toProductEntity(product)
        productDao.insertProduct(entity)
        shoppingListsDao.updateLastModified(entity.shoppingUid, entity.lastModified)
    }

    override suspend fun editProduct(product: Product): Unit = withContext(AppDispatchers.IO) {
        val entity = mapping.toProductEntity(product)
        productDao.insertProduct(entity)
        shoppingListsDao.updateLastModified(entity.shoppingUid, entity.lastModified)
    }

    override suspend fun addAutocomplete(autocomplete: Autocomplete): Unit = withContext(AppDispatchers.IO) {
        val entity = mapping.toAutocompleteEntity(autocomplete)
        autocompletesDao.insertAutocomplete(entity)
    }

    override suspend fun lockProductQuantity(): Unit = withContext(AppDispatchers.IO) {
        val value = mapping.toLockProductElementName(LockProductElement.QUANTITY)
        appConfigDao.lockProductElement(value)
    }

    override suspend fun lockProductPrice(): Unit = withContext(AppDispatchers.IO) {
        val value = mapping.toLockProductElementName(LockProductElement.PRICE)
        appConfigDao.lockProductElement(value)
    }

    override suspend fun lockProductTotal(): Unit = withContext(AppDispatchers.IO) {
        val value = mapping.toLockProductElementName(LockProductElement.TOTAL)
        appConfigDao.lockProductElement(value)
    }
}