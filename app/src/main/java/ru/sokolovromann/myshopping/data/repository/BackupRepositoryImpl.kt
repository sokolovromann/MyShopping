package ru.sokolovromann.myshopping.data.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.AppConfigDao
import ru.sokolovromann.myshopping.data.local.dao.BackupDao
import ru.sokolovromann.myshopping.data.local.files.BackupFiles
import ru.sokolovromann.myshopping.data.repository.model.AppConfig
import ru.sokolovromann.myshopping.data.repository.model.Backup
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
    private val backupDao: BackupDao,
    private val files: BackupFiles,
    private val appConfigDao: AppConfigDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : BackupRepository {

    override suspend fun getAppConfig(): Flow<AppConfig> = withContext(dispatchers.io) {
        return@withContext appConfigDao.getAppConfig().map {
            mapping.toAppConfig(it)
        }
    }

    override suspend fun getReminderUids(): Flow<List<String>> = withContext(dispatchers.io) {
        return@withContext backupDao.getReminderUids()
    }

    override suspend fun deleteAppData(): Result<Unit> = withContext(dispatchers.io) {
        backupDao.deleteShoppings()
        backupDao.deleteProducts()
        backupDao.deleteAutocompletes()
        return@withContext Result.success(Unit)
    }

    override suspend fun createBackup(
        currentAppVersion: Int
    ): Flow<Backup> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = backupDao.getShoppings(),
            flow2 = backupDao.getProducts(),
            flow3 = backupDao.getAutocompletes(),
            flow4 = appConfigDao.getAppConfig(),
            transform = { shoppingListEntities, productEntities, autocompleteEntities, appConfigEntity ->
                mapping.toBackup(shoppingListEntities, productEntities, autocompleteEntities, appConfigEntity, currentAppVersion)
            }
        )
    }

    override suspend fun addBackup(backup: Backup) = withContext(dispatchers.io) {
        val shoppingEntities = mapping.toShoppingEntities(backup)
        backupDao.addShoppings(shoppingEntities)

        val productEntities = mapping.toProductEntities(backup)
        backupDao.addProducts(productEntities)

        val autocompleteEntities = mapping.toAutocompleteEntities(backup)
        backupDao.addAutocompletes(autocompleteEntities)

        val appConfigEntity = mapping.toAppConfigEntity(backup.appConfig)
        appConfigDao.saveAppConfig(appConfigEntity)
    }

    override suspend fun importBackup(uri: Uri): Result<Flow<Backup>> = withContext(dispatchers.io) {
        return@withContext files.readBackup(uri).map {
            it.map { entity -> mapping.toBackup(entity) }
        }
    }

    override suspend fun exportBackup(backup: Backup): Result<String> = withContext(dispatchers.io) {
        val entity = mapping.toBackupEntity(backup)
        return@withContext files.writeBackup(entity)
    }
}