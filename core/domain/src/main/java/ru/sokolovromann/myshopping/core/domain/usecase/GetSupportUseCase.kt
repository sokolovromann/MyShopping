package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.Support
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.repository.SuggestionsRepository
import ru.sokolovromann.myshopping.core.domain.utils.SupportUtils

class GetSupportUseCase @Inject constructor(
    private val suggestionsRepository: SuggestionsRepository,
    private val observeSuggestionsPreferencesUseCase: ObserveSuggestionsPreferencesUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(uid: UID): Support? =
        withContext(ioDispatcher) {
            suggestionsRepository.getSuggestionWithFabrics(uid)?.let { suggestionWithFabrics ->
                val preferences = observeSuggestionsPreferencesUseCase().first()
                SupportUtils.createSupport(
                    suggestionWithFabrics,
                    preferences.displaySuggestionDetails
                )
            }
        }
}