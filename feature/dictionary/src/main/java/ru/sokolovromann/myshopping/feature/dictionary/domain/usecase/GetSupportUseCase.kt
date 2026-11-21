package ru.sokolovromann.myshopping.feature.dictionary.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.repository.GeneralPreferencesRepository
import ru.sokolovromann.myshopping.core.domain.repository.SuggestionsRepository
import ru.sokolovromann.myshopping.core.domain.usecase.FilterSuggestionsWithFabricsUseCase
import ru.sokolovromann.myshopping.feature.dictionary.domain.model.Support

class GetSupportUseCase @Inject constructor(
    private val suggestionsRepository: SuggestionsRepository,
    private val generalPreferencesRepository: GeneralPreferencesRepository,
    private val filterSuggestionsWithFabricsUseCase: FilterSuggestionsWithFabricsUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(uid: UID): Support =
        withContext(ioDispatcher) {
            val suggestionWithFabrics = suggestionsRepository.getSuggestionWithFabrics(uid)
                ?.let { filterSuggestionsWithFabricsUseCase(listOf(it)) }
                ?.first()
            val generalPreferences = generalPreferencesRepository.observeGeneralPreferences().first()
            Support(suggestionWithFabrics, generalPreferences)
        }
}