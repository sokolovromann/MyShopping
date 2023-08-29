package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.AddEditAutocomplete
import ru.sokolovromann.myshopping.data.repository.model.Autocomplete
import javax.inject.Inject

class AddEditAutocompleteRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping
) : AddEditAutocompleteRepository {

    private val autocompleteDao = localDatasource.getAutocompletesDao()
    private val appConfigDao = localDatasource.getAppConfigDao()

    override suspend fun getAddEditAutocomplete(
        uid: String?
    ): Flow<AddEditAutocomplete> = withContext(AppDispatchers.IO) {
        return@withContext if (uid == null) {
            appConfigDao.getAppConfig().transform {
                val value = mapping.toAddEditAutocomplete(null, it)
                emit(value)
            }
        } else {
            autocompleteDao.getAutocomplete(uid).combine(
                flow = appConfigDao.getAppConfig(),
                transform = { entity, appConfigEntity ->
                    mapping.toAddEditAutocomplete(entity, appConfigEntity)
                }
            )
        }
    }

    override suspend fun addAutocomplete(
        autocomplete: Autocomplete
    ): Unit = withContext(AppDispatchers.IO) {
        val entity = mapping.toAutocompleteEntity(autocomplete)
        autocompleteDao.insertAutocomplete(entity)
    }

    override suspend fun editAutocomplete(
        autocomplete: Autocomplete
    ): Unit = withContext(AppDispatchers.IO) {
        val entity = mapping.toAutocompleteEntity(autocomplete)
        autocompleteDao.insertAutocomplete(entity)
    }
}