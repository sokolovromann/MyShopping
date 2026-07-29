package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.CartDirectory
import ru.sokolovromann.myshopping.core.domain.model.CartWithProducts
import ru.sokolovromann.myshopping.core.domain.repository.CartsRepository

class ObserveCartsWithProductsUseCase @Inject constructor(
    private val cartsRepository: CartsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    operator fun invoke(directory: CartDirectory): Flow<Collection<CartWithProducts>> =
        cartsRepository.observeCartsWithProducts(directory).flowOn(ioDispatcher)
}