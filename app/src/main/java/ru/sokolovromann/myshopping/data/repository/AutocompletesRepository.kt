package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.Autocompletes

interface AutocompletesRepository {

    suspend fun getAutocompletes(): Flow<Autocompletes>

    suspend fun deleteAutocomplete(uid: String)
}