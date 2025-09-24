package ru.sokolovromann.myshopping.settings.general

import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext
import javax.inject.Inject

class UpdateGeneralConfigUseCase @Inject constructor(
    private val generalConfigDataStore: GeneralConfigDataStore
) {

    private val dispatcher: Dispatcher = Dispatcher.IO

    suspend operator fun invoke(generalConfig: GeneralConfig) = withContext(dispatcher) {
        generalConfigDataStore.update(generalConfig)
    }
}