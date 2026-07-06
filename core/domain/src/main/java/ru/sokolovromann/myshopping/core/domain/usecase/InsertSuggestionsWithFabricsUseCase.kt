package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.SuggestionWithFabrics

class InsertSuggestionsWithFabricsUseCase @Inject constructor(
    private val insertSuggestionsUseCase: InsertSuggestionsUseCase,
    private val insertFabricsUseCase: InsertFabricsUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(suggestionsWithFabrics: Collection<SuggestionWithFabrics>): Unit =
        withContext(ioDispatcher) {
            val suggestions = suggestionsWithFabrics.map { it.suggestion }
            insertSuggestionsUseCase(suggestions)

            val fabrics = suggestionsWithFabrics.flatMap { it.fabrics }
            insertFabricsUseCase(fabrics)
        }

    suspend operator fun invoke(suggestionWithFabrics: SuggestionWithFabrics): Unit =
        withContext(ioDispatcher) {
            insertSuggestionsUseCase(suggestionWithFabrics.suggestion)
            insertFabricsUseCase(suggestionWithFabrics.fabrics)
        }
}