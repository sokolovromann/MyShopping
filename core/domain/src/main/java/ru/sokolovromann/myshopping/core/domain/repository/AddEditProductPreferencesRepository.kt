package ru.sokolovromann.myshopping.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.core.domain.model.AddEditProductPreferences

interface AddEditProductPreferencesRepository {

    fun observeAddEditProductPreferences(): Flow<AddEditProductPreferences>

    suspend fun updateAddEditProductPreferences(preferences: AddEditProductPreferences)
}