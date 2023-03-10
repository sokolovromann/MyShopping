package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.Autocompletes

interface AutocompletesRepository {

    suspend fun getDefaultAutocompletes(): Flow<Autocompletes>

    suspend fun getPersonalAutocompletes(): Flow<Autocompletes>

    suspend fun deleteAutocomplete(uid: String)
}