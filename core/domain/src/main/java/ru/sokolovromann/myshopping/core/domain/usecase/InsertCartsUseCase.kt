package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.model.Cart
import ru.sokolovromann.myshopping.core.domain.repository.CartsRepository

class InsertCartsUseCase @Inject constructor(
    private val cartsRepository: CartsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend operator fun invoke(carts: Collection<Cart>): Unit = withContext(ioDispatcher) {
        cartsRepository.insertCarts(carts)
    }

    suspend operator fun invoke(cart: Cart): Unit = withContext(ioDispatcher) {
        val carts = listOf(cart)
        cartsRepository.insertCarts(carts)
    }
}