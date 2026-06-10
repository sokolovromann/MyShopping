package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.model.Product
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.repository.ProductsRepository
import kotlin.coroutines.CoroutineContext

class GetProductUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    suspend operator fun invoke(uid: UID): Product? = withContext(ioDispatcher) {
        productsRepository.getProduct(uid)
    }
}