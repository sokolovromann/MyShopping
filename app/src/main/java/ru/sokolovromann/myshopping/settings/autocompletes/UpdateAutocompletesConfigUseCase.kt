package ru.sokolovromann.myshopping.settings.autocompletes

import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext
import javax.inject.Inject

class UpdateAutocompletesConfigUseCase @Inject constructor(
    private val dataStore: AutocompletesConfigDataStore,
    private val dispatcher: Dispatcher
) {

    suspend operator fun invoke(autocompletesConfig: AutocompletesConfig) = withContext(dispatcher) {
        dataStore.update(autocompletesConfig)
    }
}