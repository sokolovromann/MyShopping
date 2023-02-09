package ru.sokolovromann.myshopping.data.local.dao

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.datasource.LocalDataStore
import ru.sokolovromann.myshopping.data.local.entity.AutocompletePreferencesEntity
import javax.inject.Inject

class AutocompletesPreferencesDao @Inject constructor(
    private val localDataStore: LocalDataStore
) {

    suspend fun getAutocompletePreferences(): Flow<AutocompletePreferencesEntity> {
        return localDataStore.getAutocompletePreferences()
    }
}