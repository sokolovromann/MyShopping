package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.Autocomplete
import ru.sokolovromann.myshopping.data.repository.model.Autocompletes

interface AutocompletesRepository {

    suspend fun getDefaultAutocompletes(): Flow<Autocompletes>

    suspend fun getPersonalAutocompletes(): Flow<Autocompletes>

    suspend fun clearAutocomplete(autocomplete: Autocomplete)

    suspend fun deleteAutocomplete(uid: String)
}