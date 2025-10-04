package ru.sokolovromann.myshopping.settings.autocompletes

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.flowOn
import javax.inject.Inject

class ObserveAutocompletesConfigUseCase @Inject constructor(
    private val dataStore: AutocompletesConfigDataStore,
    private val dispatcher: Dispatcher
) {

    operator fun invoke(): Flow<AutocompletesConfig> {
        return dataStore.observe().flowOn(dispatcher)
    }
}