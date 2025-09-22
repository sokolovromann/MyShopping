package ru.sokolovromann.myshopping.settings.carts

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.flowOn
import javax.inject.Inject

class ObserveCartsConfigUseCase @Inject constructor(
    private val cartsConfigDataStore: CartsConfigDataStore
) {

    private val dispatcher: Dispatcher = Dispatcher.IO

    operator fun invoke(): Flow<CartsConfig> {
        return cartsConfigDataStore.observe().flowOn(dispatcher)
    }
}