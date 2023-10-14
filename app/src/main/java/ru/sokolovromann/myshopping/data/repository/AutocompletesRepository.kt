package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.local.entity.AutocompleteEntity
import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.AutocompleteWithConfig
import ru.sokolovromann.myshopping.data.model.AutocompletesWithConfig
import ru.sokolovromann.myshopping.data.model.mapper.AppConfigMapper
import ru.sokolovromann.myshopping.data.model.mapper.AutocompletesMapper
import ru.sokolovromann.myshopping.data.repository.model.Time
import java.util.Locale
import javax.inject.Inject

class AutocompletesRepository @Inject constructor(localDatasource: LocalDatasource) {

    private val autocompletesDao = localDatasource.getAutocompletesDao()
    private val resourcesDao = localDatasource.getResourcesDao()
    private val appConfigDao = localDatasource.getAppConfigDao()

    private val dispatcher = AppDispatchers.IO

    suspend fun getAllAutocompletes(): Flow<AutocompletesWithConfig> = withContext(dispatcher) {
        return@withContext autocompletesDao.getAllAutocompletes().combine(
            flow = appConfigDao.getAppConfig(),
            transform = { autocompleteEntities, appConfigEntity ->
                AutocompletesMapper.toAutocompletesWithConfig(
                    entities = autocompleteEntities,
                    appConfig = AppConfigMapper.toAppConfig(appConfigEntity)
                )
            }
        )
    }

    suspend fun getDefaultAutocompletes(
        language: String = Locale.getDefault().language
    ): Flow<AutocompletesWithConfig> = withContext(dispatcher) {
        return@withContext combine(
            flow = autocompletesDao.getDefaultAutocompletes(),
            flow2 = appConfigDao.getAppConfig(),
            transform = { autocompleteEntities, appConfigEntity ->
                AutocompletesMapper.toAutocompletesWithConfig(
                    entities = autocompleteEntities,
                    resources = resourcesDao.getAutocompleteNames(),
                    appConfig = AppConfigMapper.toAppConfig(appConfigEntity),
                    language = language
                )
            }
        )
    }

    suspend fun getPersonalAutocompletes(): Flow<AutocompletesWithConfig> = withContext(dispatcher) {
        return@withContext autocompletesDao.getPersonalAutocompletes().combine(
            flow = appConfigDao.getAppConfig(),
            transform = { autocompleteEntities, appConfigEntity ->
                AutocompletesMapper.toAutocompletesWithConfig(
                    entities = autocompleteEntities,
                    appConfig = AppConfigMapper.toAppConfig(appConfigEntity)
                )
            }
        )
    }

    suspend fun getAutocomplete(uid: String?): Flow<AutocompleteWithConfig> = withContext(dispatcher) {
        return@withContext if (uid == null) {
            appConfigDao.getAppConfig().map { appConfigEntity ->
                AutocompletesMapper.toAutocompleteWithConfig(
                    appConfig = AppConfigMapper.toAppConfig(appConfigEntity)
                )
            }
        } else {
            autocompletesDao.getAutocomplete(uid).combine(
                flow = appConfigDao.getAppConfig(),
                transform = { autocompleteEntity, appConfigEntity ->
                    AutocompletesMapper.toAutocompleteWithConfig(
                        entity = autocompleteEntity ?: AutocompleteEntity(),
                        appConfig = AppConfigMapper.toAppConfig(appConfigEntity)
                    )
                }
            )
        }
    }

    suspend fun searchAutocompletesLikeName(
        search: String,
        language: String = Locale.getDefault().language
    ): Flow<List<Autocomplete>> = withContext(dispatcher) {
         combine(
            flow = autocompletesDao.searchAutocompletesLikeName(search),
            flow2 = appConfigDao.getAppConfig(),
            transform = { autocompleteEntities, appConfigEntity ->
                AutocompletesMapper.toAutocompletes(
                    entities = autocompleteEntities,
                    resources = resourcesDao.searchAutocompleteNames(search),
                    appConfig = AppConfigMapper.toAppConfig(appConfigEntity),
                    language = language
                )
            }
        )
    }

    suspend fun saveAutocompletes(autocompletes: List<Autocomplete>): Unit = withContext(dispatcher) {
        val entities = AutocompletesMapper.toAutocompleteEntities(autocompletes)
        autocompletesDao.insertAutocompletes(entities)
    }

    suspend fun saveAutocomplete(autocomplete: Autocomplete): Unit = withContext(dispatcher) {
        val entity = AutocompletesMapper.toAutocompleteEntity(autocomplete)
        autocompletesDao.insertAutocomplete(entity)
    }

    suspend fun clearAutocompletes(
        uids: List<String>,
        lastModified: Time = Time.getCurrentTime()
    ): Unit = withContext(dispatcher) {
        autocompletesDao.clearAutocompletes(uids, lastModified.millis)
    }

    suspend fun deleteAllAutocompletes(): Unit = withContext(dispatcher) {
        autocompletesDao.deleteAllAutocompletes()
    }

    suspend fun deleteAutocompletes(uids: List<String>): Unit = withContext(dispatcher) {
        autocompletesDao.deleteAutocompletes(uids)
    }
}