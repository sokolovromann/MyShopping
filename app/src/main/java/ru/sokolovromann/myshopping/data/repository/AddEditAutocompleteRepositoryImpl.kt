package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.AddEditAutocompleteDao
import ru.sokolovromann.myshopping.data.local.dao.AddEditAutocompletePreferencesDao
import ru.sokolovromann.myshopping.data.repository.model.AddEditAutocomplete
import ru.sokolovromann.myshopping.data.repository.model.Autocomplete
import javax.inject.Inject

class AddEditAutocompleteRepositoryImpl @Inject constructor(
    private val autocompleteDao: AddEditAutocompleteDao,
    private val preferencesDao: AddEditAutocompletePreferencesDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : AddEditAutocompleteRepository {

    override suspend fun getAddEditAutocomplete(
        uid: String?
    ): Flow<AddEditAutocomplete> = withContext(dispatchers.io) {
        return@withContext if (uid == null) {
            preferencesDao.getAutocompletePreferences().transform {
                val value = mapping.toAddEditAutocomplete(null, it)
                emit(value)
            }
        } else {
            autocompleteDao.getAutocomplete(uid).combine(
                flow = preferencesDao.getAutocompletePreferences(),
                transform = { entity, preferencesEntity ->
                    mapping.toAddEditAutocomplete(entity, preferencesEntity)
                }
            )
        }
    }

    override suspend fun addAutocomplete(
        autocomplete: Autocomplete
    ): Unit = withContext(dispatchers.io) {
        val entity = mapping.toAutocompleteEntity(autocomplete)
        autocompleteDao.insertAutocomplete(entity)
    }

    override suspend fun editAutocomplete(
        autocomplete: Autocomplete
    ): Unit = withContext(dispatchers.io) {
        val entity = mapping.toAutocompleteEntity(autocomplete)
        autocompleteDao.insertAutocomplete(entity)
    }
}