package ru.sokolovromann.myshopping.settings.products

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.flowOn
import javax.inject.Inject

class ObserveProductsConfigUseCase @Inject constructor(
    private val dataStore: ProductsConfigDataStore,
    private val dispatcher: Dispatcher
) {

    operator fun invoke(): Flow<ProductsConfig> {
        return dataStore.observe().flowOn(dispatcher)
    }
}