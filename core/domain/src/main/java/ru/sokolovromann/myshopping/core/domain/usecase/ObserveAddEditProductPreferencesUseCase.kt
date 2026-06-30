package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.sokolovromann.myshopping.core.domain.model.AddEditProductPreferences
import ru.sokolovromann.myshopping.core.domain.repository.AddEditProductPreferencesRepository

class ObserveAddEditProductPreferencesUseCase @Inject constructor(
    private val addEditProductPreferencesRepository: AddEditProductPreferencesRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    operator fun invoke(): Flow<AddEditProductPreferences> =
        addEditProductPreferencesRepository.observeAddEditProductPreferences().flowOn(ioDispatcher)
}