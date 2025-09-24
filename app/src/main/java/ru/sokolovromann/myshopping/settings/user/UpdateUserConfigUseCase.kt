package ru.sokolovromann.myshopping.settings.user

import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext
import javax.inject.Inject

class UpdateUserConfigUseCase @Inject constructor(
    private val userConfigDataStore: UserConfigDataStore
) {

    private val dispatcher: Dispatcher = Dispatcher.IO

    suspend operator fun invoke(userConfig: UserConfig) = withContext(dispatcher) {
        userConfigDataStore.update(userConfig)
    }
}