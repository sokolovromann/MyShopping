package ru.sokolovromann.myshopping.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences

interface GeneralPreferencesRepository {

    fun observeGeneralPreferences(): Flow<GeneralPreferences>

    suspend fun updateGeneralPreferences(preferences: GeneralPreferences)
}