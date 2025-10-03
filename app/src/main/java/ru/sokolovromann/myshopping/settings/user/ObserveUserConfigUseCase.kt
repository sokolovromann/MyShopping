package ru.sokolovromann.myshopping.settings.user

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.flowOn
import javax.inject.Inject

class ObserveUserConfigUseCase @Inject constructor(
    private val dataStore: UserConfigDataStore,
    private val dispatcher: Dispatcher
) {

    operator fun invoke(): Flow<UserConfig> {
        return dataStore.observe().flowOn(dispatcher)
    }
}