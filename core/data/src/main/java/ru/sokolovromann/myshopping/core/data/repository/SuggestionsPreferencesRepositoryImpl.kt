package ru.sokolovromann.myshopping.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.data.di.SuggestionsPreferencesDataStore
import ru.sokolovromann.myshopping.core.data.mapper.SuggestionsPreferencesMapper
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsPreferences
import ru.sokolovromann.myshopping.core.domain.repository.SuggestionsPreferencesRepository

class SuggestionsPreferencesRepositoryImpl @Inject constructor(
    @SuggestionsPreferencesDataStore private val suggestionsPreferencesDataStore: DataStore<Preferences>,
    private val suggestionsPreferencesMapper: SuggestionsPreferencesMapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SuggestionsPreferencesRepository {

    override fun observeSuggestionsPreferences(): Flow<SuggestionsPreferences> =
        suggestionsPreferencesDataStore.data
            .map { suggestionsPreferencesMapper.toModel(it) }
            .flowOn(ioDispatcher)

    override suspend fun updateSuggestionsPreferences(preferences: SuggestionsPreferences): Unit =
        withContext(ioDispatcher) {
            suggestionsPreferencesDataStore.edit {
                val newPreferences = suggestionsPreferencesMapper.toPreferences(preferences)
                it.plusAssign(newPreferences)
            }
        }
}