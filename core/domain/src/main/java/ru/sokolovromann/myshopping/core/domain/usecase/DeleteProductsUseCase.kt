package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.model.ProductDirectory
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.repository.ProductsRepository
import kotlin.coroutines.CoroutineContext

class DeleteProductsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    suspend operator fun invoke(directory: ProductDirectory): Unit = withContext(ioDispatcher) {
        productsRepository.deleteProducts(directory)
    }

    suspend operator fun invoke(uids: Collection<UID>): Unit = withContext(ioDispatcher) {
        productsRepository.deleteProducts(uids)
    }

    suspend operator fun invoke(uid: UID): Unit = withContext(ioDispatcher) {
        val uids = listOf(uid)
        productsRepository.deleteProducts(uids)
    }
}