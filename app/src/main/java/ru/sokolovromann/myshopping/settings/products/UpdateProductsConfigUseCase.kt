package ru.sokolovromann.myshopping.settings.products

import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext
import javax.inject.Inject

class UpdateProductsConfigUseCase @Inject constructor(
    private val productsConfigDataStore: ProductsConfigDataStore
) {

    private val dispatcher: Dispatcher = Dispatcher.IO

    suspend operator fun invoke(productsConfig: ProductsConfig) = withContext(dispatcher) {
        productsConfigDataStore.update(productsConfig)
    }
}