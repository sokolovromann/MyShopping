package ru.sokolovromann.myshopping.feature.dictionary.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.SortSuggestions
import ru.sokolovromann.myshopping.core.domain.model.SuggestionWithFabrics
import ru.sokolovromann.myshopping.core.domain.repository.GeneralPreferencesRepository
import ru.sokolovromann.myshopping.core.domain.repository.SuggestionsPreferencesRepository
import ru.sokolovromann.myshopping.core.domain.repository.SuggestionsRepository
import ru.sokolovromann.myshopping.core.domain.usecase.FilterSuggestionsWithFabricsUseCase
import ru.sokolovromann.myshopping.feature.dictionary.domain.model.Dictionary

class ObserveDictionaryUseCase @Inject constructor(
    private val suggestionsRepository: SuggestionsRepository,
    private val suggestionsPreferencesRepository: SuggestionsPreferencesRepository,
    private val generalPreferencesRepository: GeneralPreferencesRepository,
    private val filterSuggestionsWithFabricsUseCase: FilterSuggestionsWithFabricsUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    operator fun invoke() = combine(
        flow = suggestionsRepository.observeSuggestionsWithFabrics(),
        flow2 = suggestionsPreferencesRepository.observeSuggestionsPreferences(),
        flow3 = generalPreferencesRepository.observeGeneralPreferences(),
        transform = { suggestionsWithFabrics, suggestionsPreferences, generalPreferences ->
            val sorted = suggestionsWithFabrics.sorted(suggestionsPreferences.sort)
            Dictionary(
                filterSuggestionsWithFabricsUseCase(sorted),
                suggestionsPreferences,
                generalPreferences
            )
        }
    ).flowOn(ioDispatcher)

    private fun Collection<SuggestionWithFabrics>.sorted(
        sort: SortSuggestions
    ): Collection<SuggestionWithFabrics> = when (sort) {
        is SortSuggestions.ByName -> {
            if (sort.byAscending) {
                sortedBy { it.suggestion.name.lowercase() }
            } else {
                sortedByDescending { it.suggestion.name.lowercase() }
            }
        }
        is SortSuggestions.ByPopularity -> {
            if (sort.byAscending) {
                sortedBy { it.suggestion.used }
            } else {
                sortedByDescending { it.suggestion.used }
            }
        }
    }
}