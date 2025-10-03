package ru.sokolovromann.myshopping.settings.user

import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext
import javax.inject.Inject

class UpdateUserConfigUseCase @Inject constructor(
    private val dataStore: UserConfigDataStore,
    private val dispatcher: Dispatcher
) {

    suspend operator fun invoke(userConfig: UserConfig) = withContext(dispatcher) {
        dataStore.update(userConfig)
    }
}