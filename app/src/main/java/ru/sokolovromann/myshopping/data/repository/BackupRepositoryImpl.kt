package ru.sokolovromann.myshopping.data.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.AppConfig
import ru.sokolovromann.myshopping.data.repository.model.Backup
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping
) : BackupRepository {

    private val shoppingListsDao = localDatasource.getShoppingListsDao()
    private val productsDao = localDatasource.getProductsDao()
    private val autocompletesDao = localDatasource.getAutocompletesDao()
    private val appConfigDao = localDatasource.getAppConfigDao()
    private val backupFiles = localDatasource.getBackupFiles()

    override suspend fun getAppConfig(): Flow<AppConfig> = withContext(AppDispatchers.IO) {
        return@withContext appConfigDao.getAppConfig().map {
            mapping.toAppConfig(it)
        }
    }

    override suspend fun getReminderUids(): Flow<List<String>> = withContext(AppDispatchers.IO) {
        return@withContext shoppingListsDao.getReminders().map { shoppingLists ->
            shoppingLists.map { it.shoppingEntity.uid }
        }
    }

    override suspend fun deleteAppData(): Result<Unit> = withContext(AppDispatchers.IO) {
        shoppingListsDao.deleteAllShoppings()
        productsDao.deleteAllProducts()
        autocompletesDao.deleteAllAutocompletes()
        return@withContext Result.success(Unit)
    }

    override suspend fun createBackup(
        currentAppVersion: Int
    ): Flow<Backup> = withContext(AppDispatchers.IO) {
        return@withContext combine(
            flow = shoppingListsDao.getAllShoppingLists(),
            flow2 = autocompletesDao.getAllAutocompletes(),
            flow3 = appConfigDao.getAppConfig(),
            transform = { shoppingLists, autocompletes, appConfig ->
                mapping.toBackup(shoppingLists, autocompletes, appConfig, currentAppVersion)
            }
        )
    }

    override suspend fun addBackup(backup: Backup) = withContext(AppDispatchers.IO) {
        val shoppings = mapping.toShoppingEntities(backup)
        shoppingListsDao.insertShoppings(shoppings)

        val products = mapping.toProductEntities(backup)
        productsDao.insertProducts(products)

        val autocompletes = mapping.toAutocompleteEntities(backup)
        autocompletesDao.insertAutocompletes(autocompletes)

        val appConfig = mapping.toAppConfigEntity(backup.appConfig)
        appConfigDao.saveAppConfig(appConfig)
    }

    override suspend fun importBackup(uri: Uri): Result<Flow<Backup>> = withContext(AppDispatchers.IO) {
        return@withContext backupFiles.readBackup(uri).map {
            it.map { entity -> mapping.toBackup(entity) }
        }
    }

    override suspend fun exportBackup(backup: Backup): Result<String> = withContext(AppDispatchers.IO) {
        val entity = mapping.toBackupEntity(backup)
        return@withContext backupFiles.writeBackup(entity)
    }

    override suspend fun checkFile(uri: Uri): Result<Unit> = withContext(AppDispatchers.IO) {
        return@withContext backupFiles.checkFile(uri)
    }
}