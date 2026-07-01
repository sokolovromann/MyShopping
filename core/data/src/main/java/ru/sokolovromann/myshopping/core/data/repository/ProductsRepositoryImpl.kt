package ru.sokolovromann.myshopping.core.data.repository

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.data.datasource.ProductsDao
import ru.sokolovromann.myshopping.core.data.mapper.ProductsMapper
import ru.sokolovromann.myshopping.core.domain.model.Position
import ru.sokolovromann.myshopping.core.domain.model.Product
import ru.sokolovromann.myshopping.core.domain.model.ProductDirectory
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.repository.ProductsRepository

class ProductsRepositoryImpl @Inject constructor(
    private val productsDao: ProductsDao,
    private val productsMapper: ProductsMapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProductsRepository {

    override suspend fun getProduct(uid: UID): Product? =
        withContext(ioDispatcher) {
            productsDao.getProduct(uid.value)?.let {
                productsMapper.toModel(it)
            }
        }

    override suspend fun getCurrentProductPosition(): Position? =
        withContext(ioDispatcher) {
            productsDao.getCurrentProductPosition()?.let {
                productsMapper.toPositionOrMin(it)
            }
        }

    override suspend fun insertProducts(products: Collection<Product>): Unit =
        withContext(ioDispatcher) {
            val entities = productsMapper.toEntities(products)
            productsDao.insertProducts(entities)
        }

    override suspend fun deleteProducts(directory: ProductDirectory): Unit =
        withContext(ioDispatcher) {
            productsDao.deleteProducts(directory.value.value)
        }

    override suspend fun deleteProducts(uids: Collection<UID>): Unit =
        withContext(ioDispatcher) {
            val uidsStrings = productsMapper.toUidsStrings(uids)
            productsDao.deleteProducts(uidsStrings)
        }

    override suspend fun clearProducts(): Unit =
        withContext(ioDispatcher) {
            productsDao.clearProducts()
        }
}