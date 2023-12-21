package ru.sokolovromann.myshopping.data.repository

import android.net.Uri
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.exception.InvalidValueException
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.local.entity.AppConfigEntity
import ru.sokolovromann.myshopping.data.local.entity.BackupFileEntity
import ru.sokolovromann.myshopping.data.model.Backup
import ru.sokolovromann.myshopping.data.model.mapper.AppConfigMapper
import ru.sokolovromann.myshopping.data.model.mapper.AutocompletesMapper
import ru.sokolovromann.myshopping.data.model.mapper.BackupMapper
import ru.sokolovromann.myshopping.data.model.mapper.ShoppingListsMapper
import java.io.FileNotFoundException
import javax.inject.Inject

class BackupRepository @Inject constructor(localDatasource: LocalDatasource) {

    private val shoppingListsDao = localDatasource.getShoppingListsDao()
    private val productsDao = localDatasource.getProductsDao()
    private val autocompletesDao = localDatasource.getAutocompletesDao()
    private val appConfigDao = localDatasource.getAppConfigDao()
    private val filesDao = localDatasource.getFilesDao()

    private val dispatcher = AppDispatchers.IO

    suspend fun importBackup(uri: Uri): Result<Pair<Backup, Backup>> = withContext(dispatcher) {
        val backupFileEntity = filesDao.readBackup(uri).getOrNull()
        return@withContext if (backupFileEntity == null) {
            val exception = FileNotFoundException()
            Result.failure(exception)
        } else {
            listOf(
                async { shoppingListsDao.deleteAllShoppings() },
                async { productsDao.deleteAllProducts() },
                async { autocompletesDao.deleteAllAutocompletes() },
            ).awaitAll()

            listOf(
                async { shoppingListsDao.insertShoppings(backupFileEntity.shoppingEntities) },
                async { productsDao.insertProducts(backupFileEntity.productEntities) },
                async { autocompletesDao.insertAutocompletes(backupFileEntity.autocompleteEntities) },
                async { appConfigDao.saveAppConfig(backupFileEntity.appConfigEntity) }
            ).awaitAll()

            val success = Pair(
                first = getOldBackup(backupFileEntity),
                second = getNewBackup()
            )
            Result.success(success)
        }
    }

    suspend fun exportBackup(appCodeVersion: Int): Result<Backup> = withContext(dispatcher) {
        val backupFileEntity = combine(
            flow = shoppingListsDao.getAllShoppings(),
            flow2 = productsDao.getAllProducts(),
            flow3 = autocompletesDao.getAllAutocompletes(),
            flow4 = appConfigDao.getAppConfig(),
            transform = { shoppingEntities, productEntities, autocompleteEntities, appConfigEntity ->
                BackupMapper.toBackupFileEntity(
                    shoppingEntities = shoppingEntities,
                    productEntities = productEntities,
                    autocompleteEntities = autocompleteEntities,
                    appConfigEntity = appConfigEntity,
                    appCodeVersion = appCodeVersion
                )
            }
        ).firstOrNull()

        return@withContext if (backupFileEntity == null) {
            val exception = InvalidValueException("App data is not exists")
            Result.failure(exception)
        } else {
            val fileName = filesDao.writeBackup(backupFileEntity).getOrNull()
            if (fileName == null) {
                val exception = FileNotFoundException()
                Result.failure(exception)
            } else {
                val success = getOldBackup(backupFileEntity).copy(fileName = fileName)
                Result.success(success)
            }
        }
    }

    private fun getOldBackup(backupFileEntity: BackupFileEntity): Backup {
        val oldAppConfig = AppConfigMapper.toAppConfig(backupFileEntity.appConfigEntity)
        return BackupMapper.toBackup(
            shoppings = ShoppingListsMapper.toShoppings(
                shoppingEntities = backupFileEntity.shoppingEntities,
                productEntities = backupFileEntity.productEntities,
                appConfig = oldAppConfig
            ),
            products = ShoppingListsMapper.toProducts(
                entities = backupFileEntity.productEntities,
                appConfig = oldAppConfig
            ),
            autocompletes = AutocompletesMapper.toAutocompletes(
                entities = backupFileEntity.autocompleteEntities,
                resources = null,
                appConfig = oldAppConfig,
                language = null
            ),
            appConfig = oldAppConfig,
            fileName = ""
        )
    }

    private suspend fun getNewBackup(): Backup {
        val shoppingEntities = shoppingListsDao.getAllShoppings().firstOrNull() ?: listOf()
        val productEntities = productsDao.getAllProducts().firstOrNull() ?: listOf()
        val autocompleteEntities = autocompletesDao.getAllAutocompletes().firstOrNull() ?: listOf()
        val appConfigEntity = appConfigDao.getAppConfig().firstOrNull() ?: AppConfigEntity()
        val newAppConfig = AppConfigMapper.toAppConfig(appConfigEntity)

        return BackupMapper.toBackup(
            shoppings = ShoppingListsMapper.toShoppings(
                shoppingEntities = shoppingEntities,
                productEntities = productEntities,
                appConfig = newAppConfig
            ),
            products = ShoppingListsMapper.toProducts(
                entities = productEntities,
                appConfig = newAppConfig
            ),
            autocompletes = AutocompletesMapper.toAutocompletes(
                entities = autocompleteEntities,
                resources = null,
                appConfig = newAppConfig,
                language = null
            ),
            appConfig = newAppConfig,
            fileName = ""
        )
    }
}