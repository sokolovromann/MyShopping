package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.sokolovromann.myshopping.core.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.Dictionary
import ru.sokolovromann.myshopping.core.domain.model.SortSuggestions
import ru.sokolovromann.myshopping.core.domain.model.SuggestionWithFabrics
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsPreferences
import ru.sokolovromann.myshopping.core.domain.model.Support
import ru.sokolovromann.myshopping.core.domain.utils.SupportUtils

class ObserveDictionaryUseCase @Inject constructor(
    private val observeSuggestionsWithFabricsUseCase: ObserveSuggestionsWithFabricsUseCase,
    private val observeSuggestionsPreferencesUseCase: ObserveSuggestionsPreferencesUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    operator fun invoke(): Flow<Dictionary> = combine(
        flow = observeSuggestionsWithFabricsUseCase(),
        flow2 = observeSuggestionsPreferencesUseCase(),
        transform = { suggestionsWithFabrics, suggestionsPreferences ->
            Dictionary(
                suggestionsWithFabrics
                    .mapped(suggestionsPreferences)
                    .sorted(suggestionsPreferences.sort)
            )
        }
    ).flowOn(ioDispatcher)

    operator fun invoke(name: String): Flow<Dictionary> =
        invoke().map { dictionary ->
            Dictionary(
                dictionary.supports.filter { it.name.contains(name) }
            )
        }

    private fun Collection<SuggestionWithFabrics>.mapped(
        suggestionsPreferences: SuggestionsPreferences
    ) = map { suggestionWithFabrics ->
        SupportUtils.createSupport(
            suggestionWithFabrics,
            suggestionsPreferences.displaySuggestionDetails
        )
    }

    private fun Collection<Support>.sorted(
        sort: SortSuggestions
    ): Collection<Support> = when (sort) {
        is SortSuggestions.ByName -> {
            if (sort.byAscending) {
                sortedBy { it.name.lowercase() }
            } else {
                sortedByDescending { it.name.lowercase() }
            }
        }
        is SortSuggestions.ByPopularity -> {
            if (sort.byAscending) {
                sortedBy { it.used }
            } else {
                sortedByDescending { it.used }
            }
        }
    }
}