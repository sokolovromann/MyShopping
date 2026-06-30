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
import ru.sokolovromann.myshopping.core.data.di.AddEditProductPreferencesDataStore
import ru.sokolovromann.myshopping.core.data.mapper.AddEditProductPreferencesMapper
import ru.sokolovromann.myshopping.core.domain.model.AddEditProductPreferences
import ru.sokolovromann.myshopping.core.domain.repository.AddEditProductPreferencesRepository

class AddEditProductPreferencesRepositoryImpl @Inject constructor(
    @AddEditProductPreferencesDataStore private val addEditProductPreferencesDataStore: DataStore<Preferences>,
    private val addEditProductPreferencesMapper: AddEditProductPreferencesMapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AddEditProductPreferencesRepository {

    override fun observeAddEditProductPreferences(): Flow<AddEditProductPreferences> =
        addEditProductPreferencesDataStore.data
            .map { addEditProductPreferencesMapper.toModel(it) }
            .flowOn(ioDispatcher)

    override suspend fun updateAddEditProductPreferences(preferences: AddEditProductPreferences): Unit =
        withContext(ioDispatcher) {
            addEditProductPreferencesDataStore.edit {
                val newPreferences = addEditProductPreferencesMapper.toPreferences(preferences)
                it.plusAssign(newPreferences)
            }
        }
}