package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.AutocompletesDao
import ru.sokolovromann.myshopping.data.local.dao.AutocompletesPreferencesDao
import ru.sokolovromann.myshopping.data.repository.model.Autocompletes
import javax.inject.Inject

class AutocompletesRepositoryImpl @Inject constructor(
    private val autocompletesDao: AutocompletesDao,
    private val preferencesDao: AutocompletesPreferencesDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : AutocompletesRepository {

    override suspend fun getAutocompletes(): Flow<Autocompletes> = withContext(dispatchers.io) {
        return@withContext autocompletesDao.getAutocompletes().combine(
            flow = preferencesDao.getAppPreferences(),
            transform = { entities, preferencesEntity ->
                mapping.toAutocompletes(entities, preferencesEntity)
            }
        )
    }

    override suspend fun deleteAutocomplete(uid: String): Unit = withContext(dispatchers.io) {
        autocompletesDao.deleteAutocomplete(uid)
    }
}