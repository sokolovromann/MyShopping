package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.AutocompletesDao
import ru.sokolovromann.myshopping.data.local.dao.AutocompletesPreferencesDao
import ru.sokolovromann.myshopping.data.local.resources.AutocompletesResources
import ru.sokolovromann.myshopping.data.repository.model.Autocomplete
import ru.sokolovromann.myshopping.data.repository.model.Autocompletes
import javax.inject.Inject

class AutocompletesRepositoryImpl @Inject constructor(
    private val autocompletesDao: AutocompletesDao,
    private val resources: AutocompletesResources,
    private val preferencesDao: AutocompletesPreferencesDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : AutocompletesRepository {

    override suspend fun getDefaultAutocompletes(
        language: String
    ): Flow<Autocompletes> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = autocompletesDao.getDefaultAutocompletes(),
            flow2 = resources.getDefaultAutocompleteNames(),
            flow3 = preferencesDao.getAppPreferences(),
            transform = { entities, names, preferencesEntity ->
                mapping.toAutocompletes(entities, names, preferencesEntity, language)
            }
        )
    }

    override suspend fun getPersonalAutocompletes(): Flow<Autocompletes> = withContext(dispatchers.io) {
        return@withContext autocompletesDao.getPersonalAutocompletes().combine(
            flow = preferencesDao.getAppPreferences(),
            transform = { entities, preferencesEntity ->
                mapping.toAutocompletes(entities, null, preferencesEntity, null)
            }
        )
    }

    override suspend fun clearAutocompletes(autocompletes: List<Autocomplete>): Unit = withContext(dispatchers.io) {
        autocompletes.forEach {
            val entity = mapping.toAutocompleteEntity(it)
            autocompletesDao.insertAutocomplete(entity)
        }
    }

    override suspend fun deleteAutocompletes(uids: List<String>): Unit = withContext(dispatchers.io) {
        uids.forEach { autocompletesDao.deleteAutocomplete(it) }
    }
}