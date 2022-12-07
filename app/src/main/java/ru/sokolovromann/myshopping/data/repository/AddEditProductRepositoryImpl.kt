package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.AddEditProductDao
import ru.sokolovromann.myshopping.data.local.dao.AddEditProductPreferencesDao
import ru.sokolovromann.myshopping.data.local.resources.AddEditProductsResources
import ru.sokolovromann.myshopping.data.repository.model.AddEditProduct
import ru.sokolovromann.myshopping.data.repository.model.AddEditProductAutocomplete
import ru.sokolovromann.myshopping.data.repository.model.Autocomplete
import ru.sokolovromann.myshopping.data.repository.model.Product
import javax.inject.Inject

class AddEditProductRepositoryImpl @Inject constructor(
    private val productDao: AddEditProductDao,
    private val resources: AddEditProductsResources,
    private val preferencesDao: AddEditProductPreferencesDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : AddEditProductRepository {

    override suspend fun getAddEditProduct(uid: String?): Flow<AddEditProduct> = withContext(dispatchers.io) {
        return@withContext if (uid == null) {
            preferencesDao.getProductPreferences().transform {
                val value = mapping.toAddEditProduct(null, it)
                emit(value)
            }
        } else {
            productDao.getProduct(uid).combine(
                flow = preferencesDao.getProductPreferences(),
                transform = { entity, preferencesEntity ->
                    mapping.toAddEditProduct(entity, preferencesEntity)
                }
            )
        }
    }

    override suspend fun getAutocompletes(
        search: String
    ): Flow<AddEditProductAutocomplete> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = productDao.getAutocompletes(search),
            flow2 = resources.getDefaultAutocompleteNames(search),
            flow3 = preferencesDao.getProductPreferences(),
            transform = { entities, resources, preferencesEntity ->
                mapping.toAddEditProductAutocomplete(entities, resources, preferencesEntity)
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

    override suspend fun invertProductsLockQuantity(): Unit = withContext(dispatchers.io) {
        preferencesDao.invertProductsLockQuantity()
    }
}