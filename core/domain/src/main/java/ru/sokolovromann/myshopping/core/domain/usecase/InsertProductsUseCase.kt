package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.model.Product
import ru.sokolovromann.myshopping.core.domain.repository.ProductsRepository
import kotlin.coroutines.CoroutineContext

class InsertProductsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    suspend operator fun invoke(products: Collection<Product>): Unit = withContext(ioDispatcher) {
        productsRepository.insertProducts(products)
    }

    suspend operator fun invoke(product: Product): Unit = withContext(ioDispatcher) {
        val products = listOf(product)
        productsRepository.insertProducts(products)
    }
}