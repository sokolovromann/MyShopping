package ru.sokolovromann.myshopping.settings.carts

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.flowOn
import javax.inject.Inject

class ObserveCartsConfigUseCase @Inject constructor(
    private val dataStore: CartsConfigDataStore,
    private val dispatcher: Dispatcher
) {

    operator fun invoke(): Flow<CartsConfig> {
        return dataStore.observe().flowOn(dispatcher)
    }
}