package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.exception.InvalidNameException
import ru.sokolovromann.myshopping.data.exception.InvalidUidException
import ru.sokolovromann.myshopping.data.exception.InvalidValueException
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.local.entity.AutocompleteEntity
import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.AutocompleteWithConfig
import ru.sokolovromann.myshopping.data.model.AutocompletesWithConfig
import ru.sokolovromann.myshopping.data.model.mapper.AppConfigMapper
import ru.sokolovromann.myshopping.data.model.mapper.AutocompletesMapper
import ru.sokolovromann.myshopping.data.model.DateTime
import java.util.Locale
import javax.inject.Inject

class AutocompletesRepository @Inject constructor(localDatasource: LocalDatasource) {

    private val autocompletesDao = localDatasource.getAutocompletesDao()
    private val resourcesDao = localDatasource.getResourcesDao()
    private val appConfigDao = localDatasource.getAppConfigDao()

    private val dispatcher = AppDispatchers.IO

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

    suspend fun saveAutocompletes(autocompletes: List<Autocomplete>): Result<Unit> = withContext(dispatcher) {
        return@withContext if (autocompletes.isEmpty()) {
            val exception = InvalidValueException("List must not be empty")
            Result.failure(exception)
        } else {
            val entities = AutocompletesMapper.toAutocompleteEntities(autocompletes)
            autocompletesDao.insertAutocompletes(entities)
            Result.success(Unit)
        }
    }

    suspend fun saveAutocomplete(autocomplete: Autocomplete): Result<Unit> = withContext(dispatcher) {
        return@withContext if (autocomplete.name.isEmpty()) {
            val exception = InvalidNameException("Name must not be empty")
            Result.failure(exception)
        } else {
            val entity = AutocompletesMapper.toAutocompleteEntity(autocomplete)
            autocompletesDao.insertAutocomplete(entity)
            Result.success(Unit)
        }
    }

    suspend fun clearAutocompletes(
        uids: List<String>,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        return@withContext if (uids.isEmpty()) {
            val exception = InvalidUidException("Uids must not be empty")
            Result.failure(exception)
        } else {
            autocompletesDao.clearAutocompletes(uids, lastModified.millis)
            Result.success(Unit)
        }
    }

    suspend fun deleteAutocompletes(uids: List<String>): Result<Unit> = withContext(dispatcher) {
        return@withContext if (uids.isEmpty()) {
            val exception = InvalidUidException("Uids must not be empty")
            Result.failure(exception)
        } else {
            autocompletesDao.deleteAutocompletes(uids)
            Result.success(Unit)
        }
    }
}