package ru.sokolovromann.myshopping.settings.addeditproduct

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.flowOn
import javax.inject.Inject

class ObserveAddEditProductConfigUseCase @Inject constructor(
    private val dataStore: AddEditProductConfigDataStore,
    private val dispatcher: Dispatcher
) {

    operator fun invoke(): Flow<AddEditProductConfig> {
        return dataStore.observe().flowOn(dispatcher)
    }
}