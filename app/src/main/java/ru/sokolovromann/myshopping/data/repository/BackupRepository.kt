package ru.sokolovromann.myshopping.data.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.Backup
import javax.inject.Inject

class BackupRepository @Inject constructor(localDatasource: LocalDatasource) {

    private val shoppingListsDao = localDatasource.getShoppingListsDao()
    private val autocompletesDao = localDatasource.getAutocompletesDao()
    private val appConfigDao = localDatasource.getAppConfigDao()
    private val backupFiles = localDatasource.getBackupFiles()

    private val dispatcher = AppDispatchers.IO

    suspend fun createBackup(currentAppVersion: Int): Flow<Backup> = withContext(dispatcher) {
        return@withContext combine(
            flow = shoppingListsDao.getAllShoppingLists(),
            flow2 = autocompletesDao.getAllAutocompletes(),
            flow3 = appConfigDao.getAppConfig(),
            transform = { shoppingLists, autocompletes, appConfig ->
                RepositoryMapper.toBackup(shoppingLists, autocompletes, appConfig, currentAppVersion)
            }
        )
    }

    suspend fun importBackup(uri: Uri): Result<Flow<Backup>> = withContext(dispatcher) {
        return@withContext backupFiles.readBackup(uri).map {
            it.map { entity -> RepositoryMapper.toBackup(entity) }
        }
    }

    suspend fun exportBackup(backup: Backup): Result<String> = withContext(dispatcher) {
        val entity = RepositoryMapper.toBackupEntity(backup)
        return@withContext backupFiles.writeBackup(entity)
    }
}