package ru.sokolovromann.myshopping.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.core.domain.model.Suggestion
import ru.sokolovromann.myshopping.core.domain.model.SuggestionWithFabrics
import ru.sokolovromann.myshopping.core.domain.model.UID

interface SuggestionsRepository {

    fun observeSuggestionsWithFabrics(): Flow<Collection<SuggestionWithFabrics>>

    suspend fun getSuggestionWithFabrics(uid: UID): SuggestionWithFabrics?

    suspend fun findSuggestionsWithFabrics(name: String): Collection<SuggestionWithFabrics>

    suspend fun insertSuggestions(suggestions: Collection<Suggestion>)

    suspend fun deleteSuggestions(uids: Collection<UID>)

    suspend fun clearSuggestions()
}