package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.Cart
import ru.sokolovromann.myshopping.core.domain.repository.CartsRepository

class InsertCartsUseCase @Inject constructor(
    private val cartsRepository: CartsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(carts: Collection<Cart>): Unit =
        withContext(ioDispatcher) {
            cartsRepository.insertCarts(carts)
        }

    suspend operator fun invoke(cart: Cart): Unit =
        withContext(ioDispatcher) {
            val carts = listOf(cart)
            cartsRepository.insertCarts(carts)
        }
}