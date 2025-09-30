package ru.sokolovromann.myshopping.settings.addeditproduct

import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext
import javax.inject.Inject

class UpdateAddEditProductConfigUseCase @Inject constructor(
    private val dataStore: AddEditProductConfigDataStore,
    private val dispatcher: Dispatcher
) {

    suspend operator fun invoke(addEditProductConfig: AddEditProductConfig) = withContext(dispatcher) {
        dataStore.update(addEditProductConfig)
    }
}