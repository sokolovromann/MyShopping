package ru.sokolovromann.myshopping.core.data.repository

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.data.datasource.CartsDao
import ru.sokolovromann.myshopping.core.data.mapper.CartsMapper
import ru.sokolovromann.myshopping.core.data.mapper.CartsWithProductsMapper
import ru.sokolovromann.myshopping.core.domain.model.Cart
import ru.sokolovromann.myshopping.core.domain.model.CartDirectory
import ru.sokolovromann.myshopping.core.domain.model.CartWithProducts
import ru.sokolovromann.myshopping.core.domain.model.Position
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.repository.CartsRepository

class CartsRepositoryImpl @Inject constructor(
    private val cartsDao: CartsDao,
    private val cartsWithProductsMapper: CartsWithProductsMapper,
    private val cartsMapper: CartsMapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CartsRepository {

    override fun observeCartsWithProducts(directory: CartDirectory): Flow<Collection<CartWithProducts>> =
        cartsDao.observeCartsWithProducts(directory.toString())
            .map { cartsWithProductsMapper.toModels(it) }
            .flowOn(ioDispatcher)

    override fun observeCartWithProducts(uid: UID): Flow<CartWithProducts?> =
        cartsDao.observeCartWithProducts(uid.value).map { cartWithProductsEntity ->
            cartWithProductsEntity?.let { cartsWithProductsMapper.toModel(it) }
        }.flowOn(ioDispatcher)

    override suspend fun getCart(uid: UID): Cart? =
        withContext(ioDispatcher) {
            cartsDao.getCart(uid.value)?.let {
                cartsMapper.toModel(it)
            }
        }

    override suspend fun getCurrentCartPosition(): Position? =
        withContext(ioDispatcher) {
            cartsDao.getCurrentCartPosition()?.let {
                cartsMapper.toPositionOrMin(it)
            }
        }

    override suspend fun insertCarts(carts: Collection<Cart>): Unit =
        withContext(ioDispatcher) {
            val entities = cartsMapper.toEntities(carts)
            cartsDao.insertCarts(entities)
        }

    override suspend fun deleteCarts(directory: CartDirectory): Unit =
        withContext(ioDispatcher) {
            cartsDao.deleteCarts(directory.toString())
        }

    override suspend fun deleteCarts(uids: Collection<UID>): Unit =
        withContext(ioDispatcher) {
            val uidsStrings = cartsMapper.toUidsStrings(uids)
            cartsDao.deleteCarts(uidsStrings)
        }

    override suspend fun clearCarts(): Unit =
        withContext(ioDispatcher) {
            cartsDao.clearCarts()
        }
}