package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.model.DisplaySuggestionDetails
import ru.sokolovromann.myshopping.core.domain.model.DisplaySuggestionNames
import ru.sokolovromann.myshopping.core.domain.model.SortSuggestions
import ru.sokolovromann.myshopping.core.domain.model.SuggestionAddingMode
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsPreferences
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsView
import ru.sokolovromann.myshopping.core.domain.repository.SuggestionsPreferencesRepository

class UpdateSuggestionsPreferencesUseCase @Inject constructor(
    private val suggestionsPreferencesRepository: SuggestionsPreferencesRepository,
    private val observeSuggestionsPreferencesUseCase: ObserveSuggestionsPreferencesUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend operator fun invoke(preferences: SuggestionsPreferences): Unit =
        withContext(ioDispatcher) {
            suggestionsPreferencesRepository.updateSuggestionsPreferences(preferences)
        }

    suspend operator fun invoke(view: SuggestionsView): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(view = view)
            suggestionsPreferencesRepository.updateSuggestionsPreferences(preferences)
        }

    suspend operator fun invoke(sort: SortSuggestions): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(sort = sort)
            suggestionsPreferencesRepository.updateSuggestionsPreferences(preferences)
        }

    suspend operator fun invoke(addingMode: SuggestionAddingMode): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(addingMode = addingMode)
            suggestionsPreferencesRepository.updateSuggestionsPreferences(preferences)
        }

    suspend operator fun invoke(displayNames: DisplaySuggestionNames): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(displaySuggestionNames = displayNames)
            suggestionsPreferencesRepository.updateSuggestionsPreferences(preferences)
        }

    suspend operator fun invoke(displayDetails: DisplaySuggestionDetails): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(displaySuggestionDetails = displayDetails)
            suggestionsPreferencesRepository.updateSuggestionsPreferences(preferences)
        }

    private suspend fun getPreferences() = observeSuggestionsPreferencesUseCase().first()
}