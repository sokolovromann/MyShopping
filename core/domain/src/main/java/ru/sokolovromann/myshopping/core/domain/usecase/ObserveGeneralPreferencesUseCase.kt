package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.repository.GeneralPreferencesRepository

class ObserveGeneralPreferencesUseCase @Inject constructor(
    private val generalPreferencesRepository: GeneralPreferencesRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    operator fun invoke(): Flow<GeneralPreferences> =
        generalPreferencesRepository.observeGeneralPreferences().flowOn(ioDispatcher)
}