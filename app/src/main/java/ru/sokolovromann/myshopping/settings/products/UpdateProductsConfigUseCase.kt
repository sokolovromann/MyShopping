package ru.sokolovromann.myshopping.settings.products

import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext
import javax.inject.Inject

class UpdateProductsConfigUseCase @Inject constructor(
    private val dataStore: ProductsConfigDataStore,
    private val dispatcher: Dispatcher
) {

    suspend operator fun invoke(productsConfig: ProductsConfig) = withContext(dispatcher) {
        dataStore.update(productsConfig)
    }
}