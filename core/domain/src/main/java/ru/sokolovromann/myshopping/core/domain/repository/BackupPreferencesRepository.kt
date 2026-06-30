package ru.sokolovromann.myshopping.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.core.domain.model.BackupPreferences

interface BackupPreferencesRepository {

    fun observeBackupPreferences(): Flow<BackupPreferences>

    suspend fun updateBackupPreferences(preferences: BackupPreferences)
}