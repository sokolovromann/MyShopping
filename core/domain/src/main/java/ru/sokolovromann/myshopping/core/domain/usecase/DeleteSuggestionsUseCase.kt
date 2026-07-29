package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.repository.SuggestionsRepository

class DeleteSuggestionsUseCase @Inject constructor(
    private val suggestionsRepository: SuggestionsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(uids: Collection<UID>): Unit =
        withContext(ioDispatcher) {
            suggestionsRepository.deleteSuggestions(uids)
        }
}