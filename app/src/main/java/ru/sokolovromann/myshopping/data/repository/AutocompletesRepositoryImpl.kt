package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.Autocomplete
import ru.sokolovromann.myshopping.data.repository.model.Autocompletes
import javax.inject.Inject

class AutocompletesRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping
) : AutocompletesRepository {

    private val autocompletesDao = localDatasource.getAutocompletesDao()
    private val appConfigDao = localDatasource.getAppConfigDao()
    private val resourcesDao = localDatasource.getResourcesDao()

    override suspend fun getDefaultAutocompletes(
        language: String
    ): Flow<Autocompletes> = withContext(AppDispatchers.IO) {
        return@withContext combine(
            flow = autocompletesDao.getDefaultAutocompletes(),
            flow2 = appConfigDao.getAppConfig(),
            transform = { entities, appConfigEntity ->
                val names = resourcesDao.getAutocompleteNames()
                mapping.toAutocompletes(entities, names, appConfigEntity, language)
            }
        )
    }

    override suspend fun getPersonalAutocompletes(): Flow<Autocompletes> = withContext(AppDispatchers.IO) {
        return@withContext autocompletesDao.getPersonalAutocompletes().combine(
            flow = appConfigDao.getAppConfig(),
            transform = { entities, appConfigEntity ->
                mapping.toAutocompletes(entities, null, appConfigEntity, null)
            }
        )
    }

    override suspend fun clearAutocompletes(autocompletes: List<Autocomplete>): Unit = withContext(AppDispatchers.IO) {
        val uids = autocompletes.map { it.uid }
        autocompletesDao.clearAutocompletes(uids)
    }

    override suspend fun deleteAutocompletes(uids: List<String>): Unit = withContext(AppDispatchers.IO) {
        uids.forEach { autocompletesDao.deleteAutocomplete(it) }
    }
}