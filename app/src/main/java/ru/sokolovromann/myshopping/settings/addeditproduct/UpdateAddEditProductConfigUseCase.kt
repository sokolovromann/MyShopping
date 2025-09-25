package ru.sokolovromann.myshopping.settings.addeditproduct

import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext
import javax.inject.Inject

class UpdateAddEditProductConfigUseCase @Inject constructor(
    private val addEditProductConfigDataStore: AddEditProductConfigDataStore
) {

    private val dispatcher: Dispatcher = Dispatcher.IO

    suspend operator fun invoke(addEditProductConfig: AddEditProductConfig) = withContext(dispatcher) {
        addEditProductConfigDataStore.update(addEditProductConfig)
    }
}