package ru.sokolovromann.myshopping.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsPreferences

interface SuggestionsPreferencesRepository {

    fun observeSuggestionsPreferences(): Flow<SuggestionsPreferences>

    suspend fun updateSuggestionsPreferences(preferences: SuggestionsPreferences)
}