package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.model.Position
import ru.sokolovromann.myshopping.core.domain.repository.ProductsRepository
import kotlin.coroutines.CoroutineContext

class GetCurrentProductPositionUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    suspend operator fun invoke(): Position? = withContext(ioDispatcher) {
        productsRepository.getCurrentProductPosition()
    }
}