package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.sokolovromann.myshopping.core.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.SuggestionWithFabrics
import ru.sokolovromann.myshopping.core.domain.repository.SuggestionsRepository

class ObserveSuggestionsWithFabricsUseCase @Inject constructor(
    private val suggestionsRepository: SuggestionsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    operator fun invoke(): Flow<Collection<SuggestionWithFabrics>> =
        suggestionsRepository.observeSuggestionsWithFabrics().flowOn(ioDispatcher)
}