package ru.sokolovromann.myshopping.settings.carts

import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext
import javax.inject.Inject

class UpdateCartsConfigUseCase @Inject constructor(
    private val cartsConfigDataStore: CartsConfigDataStore
) {

    private val dispatcher: Dispatcher = Dispatcher.IO

    suspend operator fun invoke(cartsConfig: CartsConfig) = withContext(dispatcher) {
        cartsConfigDataStore.update(cartsConfig)
    }
}