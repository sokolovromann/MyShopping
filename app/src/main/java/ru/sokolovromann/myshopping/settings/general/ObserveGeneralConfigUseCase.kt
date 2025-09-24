package ru.sokolovromann.myshopping.settings.general

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.flowOn
import javax.inject.Inject

class ObserveGeneralConfigUseCase @Inject constructor(
    private val generalConfigDataStore: GeneralConfigDataStore
) {

    private val dispatcher: Dispatcher = Dispatcher.IO

    operator fun invoke(): Flow<GeneralConfig> {
        return generalConfigDataStore.observe().flowOn(dispatcher)
    }
}