package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsPreferences
import ru.sokolovromann.myshopping.core.domain.repository.SuggestionsPreferencesRepository

class ObserveSuggestionsPreferencesUseCase @Inject constructor(
    private val suggestionsPreferencesRepository: SuggestionsPreferencesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    operator fun invoke(): Flow<SuggestionsPreferences> =
        suggestionsPreferencesRepository.observeSuggestionsPreferences().flowOn(ioDispatcher)
}