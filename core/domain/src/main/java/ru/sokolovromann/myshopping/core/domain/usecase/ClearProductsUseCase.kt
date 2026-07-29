package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.repository.ProductsRepository
import kotlin.coroutines.CoroutineContext

class ClearProductsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) {

    suspend operator fun invoke(): Unit =
        withContext(ioDispatcher) {
            productsRepository.clearProducts()
        }
}