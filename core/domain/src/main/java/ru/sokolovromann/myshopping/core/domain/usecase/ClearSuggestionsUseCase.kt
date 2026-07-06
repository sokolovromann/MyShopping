package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.repository.SuggestionsRepository

class ClearSuggestionsUseCase @Inject constructor(
    private val suggestionsRepository: SuggestionsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(): Unit = withContext(ioDispatcher) {
        suggestionsRepository.clearSuggestions()
    }
}