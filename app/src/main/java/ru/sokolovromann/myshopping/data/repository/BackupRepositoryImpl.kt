package ru.sokolovromann.myshopping.data.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.AppConfig
import ru.sokolovromann.myshopping.data.repository.model.Backup
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : BackupRepository {

    private val shoppingListsDao = localDatasource.getShoppingListsDao()
    private val productsDao = localDatasource.getProductsDao()
    private val autocompletesDao = localDatasource.getAutocompletesDao()
    private val appConfigDao = localDatasource.getAppConfigDao()
    private val backupFiles = localDatasource.getBackupFiles()

    override suspend fun getAppConfig(): Flow<AppConfig> = withContext(dispatchers.io) {
        return@withContext appConfigDao.getAppConfig().map {
            mapping.toAppConfig(it)
        }
    }

    override suspend fun getReminderUids(): Flow<List<String>> = withContext(dispatchers.io) {
        return@withContext shoppingListsDao.getReminders().map { shoppingLists ->
            shoppingLists.map { it.shoppingEntity.uid }
        }
    }

    override suspend fun deleteAppData(): Result<Unit> = withContext(dispatchers.io) {
        shoppingListsDao.deleteAllShoppings()
        productsDao.deleteAllProducts()
        autocompletesDao.deleteAllAutocompletes()
        return@withContext Result.success(Unit)
    }

    override suspend fun createBackup(
        currentAppVersion: Int
    ): Flow<Backup> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = shoppingListsDao.getAllShoppingLists(),
            flow2 = autocompletesDao.getAllAutocompletes(),
            flow3 = appConfigDao.getAppConfig(),
            transform = { shoppingLists, autocompletes, appConfig ->
                mapping.toBackup(shoppingLists, autocompletes, appConfig, currentAppVersion)
            }
        )
    }

    override suspend fun addBackup(backup: Backup) = withContext(dispatchers.io) {
        val shoppings = mapping.toShoppingEntities(backup)
        shoppingListsDao.insertShoppings(shoppings)

        val products = mapping.toProductEntities(backup)
        productsDao.insertProducts(products)

        val autocompletes = mapping.toAutocompleteEntities(backup)
        autocompletesDao.insertAutocompletes(autocompletes)

        val appConfig = mapping.toAppConfigEntity(backup.appConfig)
        appConfigDao.saveAppConfig(appConfig)
    }

    override suspend fun importBackup(uri: Uri): Result<Flow<Backup>> = withContext(dispatchers.io) {
        return@withContext backupFiles.readBackup(uri).map {
            it.map { entity -> mapping.toBackup(entity) }
        }
    }

    override suspend fun exportBackup(backup: Backup): Result<String> = withContext(dispatchers.io) {
        val entity = mapping.toBackupEntity(backup)
        return@withContext backupFiles.writeBackup(entity)
    }

    override suspend fun checkFile(uri: Uri): Result<Unit> {
        return backupFiles.checkFile(uri)
    }
}