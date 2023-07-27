package ru.sokolovromann.myshopping.data.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.AppPreferences
import ru.sokolovromann.myshopping.data.repository.model.Backup

interface BackupRepository {

    suspend fun getPreferences(): Flow<AppPreferences>

    suspend fun getReminderUids(): Flow<List<String>>

    suspend fun deleteAppData(): Result<Unit>

    suspend fun createBackup(currentAppVersion: Int): Flow<Backup>

    suspend fun addBackup(backup: Backup)

    suspend fun importBackup(uri: Uri): Result<Flow<Backup>>

    suspend fun exportBackup(backup: Backup): Result<String>
}