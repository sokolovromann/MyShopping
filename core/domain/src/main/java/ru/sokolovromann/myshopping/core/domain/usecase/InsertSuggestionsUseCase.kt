package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.Suggestion
import ru.sokolovromann.myshopping.core.domain.repository.SuggestionsRepository

class InsertSuggestionsUseCase @Inject constructor(
    private val suggestionsRepository: SuggestionsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(suggestions: Collection<Suggestion>): Unit =
        withContext(ioDispatcher) {
            suggestionsRepository.insertSuggestions(suggestions)
        }

    suspend operator fun invoke(suggestion: Suggestion): Unit =
        withContext(ioDispatcher) {
            val suggestions = listOf(suggestion)
            suggestionsRepository.insertSuggestions(suggestions)
        }
}