package ru.sokolovromann.myshopping.settings.addeditproduct

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.flowOn
import javax.inject.Inject

class ObserveAddEditProductConfigUseCase @Inject constructor(
    private val addEditProductConfigDataStore: AddEditProductConfigDataStore
) {

    private val dispatcher: Dispatcher = Dispatcher.IO

    operator fun invoke(): Flow<AddEditProductConfig> {
        return addEditProductConfigDataStore.observe().flowOn(dispatcher)
    }
}