package ru.sokolovromann.myshopping.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.data.di.BackupPreferencesDataStore
import ru.sokolovromann.myshopping.core.data.mapper.BackupPreferencesMapper
import ru.sokolovromann.myshopping.core.domain.model.BackupPreferences
import ru.sokolovromann.myshopping.core.domain.repository.BackupPreferencesRepository

class BackupPreferencesRepositoryImpl @Inject constructor(
    @BackupPreferencesDataStore private val backupPreferencesDataStore: DataStore<Preferences>,
    private val backupPreferencesMapper: BackupPreferencesMapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BackupPreferencesRepository {

    override fun observeBackupPreferences(): Flow<BackupPreferences> =
        backupPreferencesDataStore.data
            .map { backupPreferencesMapper.toModel(it) }
            .flowOn(ioDispatcher)

    override suspend fun updateBackupPreferences(preferences: BackupPreferences): Unit =
        withContext(ioDispatcher) {
            backupPreferencesDataStore.edit {
                val newPreferences = backupPreferencesMapper.toPreferences(preferences)
                it.plusAssign(newPreferences)
            }
        }
}