package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.AddEditAutocomplete
import ru.sokolovromann.myshopping.data.repository.model.Autocomplete
import ru.sokolovromann.myshopping.data.repository.model.Autocompletes
import javax.inject.Inject

class AutocompletesRepository @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping
) {

    private val autocompletesDao = localDatasource.getAutocompletesDao()
    private val resourcesDao = localDatasource.getResourcesDao()
    private val appConfigDao = localDatasource.getAppConfigDao()

    private val dispatcher = AppDispatchers.IO

    suspend fun getAllAutocompletes(): Flow<Autocompletes> = withContext(dispatcher) {
        return@withContext autocompletesDao.getAllAutocompletes().combine(
            flow = appConfigDao.getAppConfig(),
            transform = { autocompletes, appConfig ->
                mapping.toAutocompletes(autocompletes, null, appConfig, null)
            }
        )
    }

    suspend fun getDefaultAutocompletes(language: String): Flow<Autocompletes> = withContext(dispatcher) {
        return@withContext combine(
            flow = autocompletesDao.getDefaultAutocompletes(),
            flow2 = appConfigDao.getAppConfig(),
            transform = { autocompletes, appConfig ->
                val resources = resourcesDao.getAutocompleteNames()
                mapping.toAutocompletes(autocompletes, resources, appConfig, language)
            }
        )
    }

    suspend fun getPersonalAutocompletes(): Flow<Autocompletes> = withContext(dispatcher) {
        return@withContext autocompletesDao.getPersonalAutocompletes().combine(
            flow = appConfigDao.getAppConfig(),
            transform = { autocompletes, appConfig ->
                mapping.toAutocompletes(autocompletes, null, appConfig, null)
            }
        )
    }

    suspend fun getAddEditAutocomplete(uid: String?): Flow<AddEditAutocomplete> = withContext(dispatcher) {
        return@withContext if (uid == null) {
            appConfigDao.getAppConfig().map {
                mapping.toAddEditAutocomplete(null, it)
            }
        } else {
            autocompletesDao.getAutocomplete(uid).combine(
                flow = appConfigDao.getAppConfig(),
                transform = { autocomplete, appConfig ->
                    mapping.toAddEditAutocomplete(autocomplete, appConfig)
                }
            )
        }
    }

    suspend fun searchAutocompletesLikeName(
        search: String,
        language: String
    ): Flow<List<Autocomplete>> = withContext(dispatcher) {
         combine(
            flow = autocompletesDao.searchAutocompletesLikeName(search),
            flow2 = appConfigDao.getAppConfig(),
            transform = { entities, appConfigEntity ->
                val resources = resourcesDao.searchAutocompleteNames(search)
                mapping.toAutocompletesList(entities, resources, appConfigEntity, language)
            }
        )
    }

    suspend fun saveAutocompletes(autocompletes: List<Autocomplete>): Unit = withContext(dispatcher) {
        val entities = mapping.toAutocompleteEntities(autocompletes)
        autocompletesDao.insertAutocompletes(entities)
    }

    suspend fun saveAutocomplete(autocomplete: Autocomplete): Unit = withContext(dispatcher) {
        val entity = mapping.toAutocompleteEntity(autocomplete)
        autocompletesDao.insertAutocomplete(entity)
    }

    suspend fun clearAutocompletes(uids: List<String>, lastModified: Long): Unit = withContext(dispatcher) {
        autocompletesDao.clearAutocompletes(uids, lastModified)
    }

    suspend fun deleteAllAutocompletes(): Unit = withContext(dispatcher) {
        autocompletesDao.deleteAllAutocompletes()
    }

    suspend fun deleteAutocompletes(uids: List<String>): Unit = withContext(dispatcher) {
        autocompletesDao.deleteAutocompletes(uids)
    }
}