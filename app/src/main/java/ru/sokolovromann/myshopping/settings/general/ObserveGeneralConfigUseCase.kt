package ru.sokolovromann.myshopping.settings.general

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.flowOn
import javax.inject.Inject

class ObserveGeneralConfigUseCase @Inject constructor(
    private val dataStore: GeneralConfigDataStore,
    private val dispatcher: Dispatcher
) {

    operator fun invoke(): Flow<GeneralConfig> {
        return dataStore.observe().flowOn(dispatcher)
    }
}