package ru.sokolovromann.myshopping.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.data.di.GeneralPreferencesDataStore
import ru.sokolovromann.myshopping.core.data.mapper.GeneralPreferencesMapper
import ru.sokolovromann.myshopping.core.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.repository.GeneralPreferencesRepository

class GeneralPreferencesRepositoryImpl @Inject constructor(
    @GeneralPreferencesDataStore private val generalPreferencesDataStore: DataStore<Preferences>,
    private val generalPreferencesMapper: GeneralPreferencesMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GeneralPreferencesRepository {

    override fun observeGeneralPreferences(): Flow<GeneralPreferences> =
        generalPreferencesDataStore.data
            .map { generalPreferencesMapper.toModel(it) }
            .flowOn(ioDispatcher)

    override suspend fun updateGeneralPreferences(preferences: GeneralPreferences): Unit =
        withContext(ioDispatcher) {
            generalPreferencesDataStore.edit {
                val newPreferences = generalPreferencesMapper.toPreferences(preferences)
                it.plusAssign(newPreferences)
            }
        }
}