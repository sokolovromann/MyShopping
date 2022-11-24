package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.AddEditAutocomplete
import ru.sokolovromann.myshopping.data.repository.model.Autocomplete

interface AddEditAutocompleteRepository {

    suspend fun getAddEditAutocomplete(uid: String?): Flow<AddEditAutocomplete>

    suspend fun addAutocomplete(autocomplete: Autocomplete)

    suspend fun editAutocomplete(autocomplete: Autocomplete)
}