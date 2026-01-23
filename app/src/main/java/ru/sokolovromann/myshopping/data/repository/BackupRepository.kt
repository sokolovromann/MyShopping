package ru.sokolovromann.myshopping.data.repository

import android.net.Uri
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import ru.sokolovromann.myshopping.data.exception.InvalidValueException
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.local.entity.AppConfigEntity
import ru.sokolovromann.myshopping.data.local.entity.BackupFileEntity
import ru.sokolovromann.myshopping.data39.old.Api15ProductEntity
import ru.sokolovromann.myshopping.data39.old.Api15ShoppingEntity
import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.model.Backup
import ru.sokolovromann.myshopping.data.model.mapper.AppConfigMapper
import ru.sokolovromann.myshopping.data.model.mapper.AutocompletesMapper
import ru.sokolovromann.myshopping.data.model.mapper.BackupMapper
import ru.sokolovromann.myshopping.data.model.mapper.ShoppingListsMapper
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext
import java.io.FileNotFoundException
import javax.inject.Inject

class BackupRepository @Inject constructor(localDatasource: LocalDatasource) {

    private val shoppingListsDao = localDatasource.getShoppingListsDao()
    private val productsDao = localDatasource.getProductsDao()
    private val autocompletesDao = localDatasource.getAutocompletesDao()
    private val appConfigDao = localDatasource.getAppConfigDao()
    private val backupDao = localDatasource.getBackupDao()

    private val dispatcher = Dispatcher.IO

    suspend fun importBackup(uri: Uri): Result<Pair<Backup, Backup>> = withContext(dispatcher) {
        val backupFileEntity = backupDao.readBackup(uri)
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
                async {
                    val shoppings = getFilteredShoppings(backupFileEntity.shoppingEntities)
                    shoppingListsDao.insertShoppings(shoppings)
                },
                async {
                    val products = getFilteredProducts(backupFileEntity.shoppingEntities, backupFileEntity.productEntities)
                    productsDao.insertProducts(products)
                },
                async { autocompletesDao.insertAutocompletes(backupFileEntity.autocompleteEntities) }
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
            transform = { shoppingEntities, productEntities, autocompleteEntities ->
                BackupMapper.toBackupFileEntity(
                    shoppingEntities = getFilteredShoppings(shoppingEntities),
                    productEntities = getFilteredProducts(shoppingEntities, productEntities),
                    autocompleteEntities = autocompleteEntities,
                    appCodeVersion = appCodeVersion
                )
            }
        ).firstOrNull()

        return@withContext if (backupFileEntity == null) {
            val exception = InvalidValueException("App data is not exists")
            Result.failure(exception)
        } else {
            val fileName = backupDao.writeBackup(backupFileEntity)
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
        val oldAppConfig = AppConfig()
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
            fileName = ""
        )
    }

    private suspend fun getNewBackup(): Backup {
        val shoppingEntities = shoppingListsDao.getAllShoppings().firstOrNull()
            ?.let { getFilteredShoppings(it) } ?: listOf()
        val productEntities = productsDao.getAllProducts().firstOrNull()
            ?.let { getFilteredProducts(shoppingEntities, it) } ?: listOf()
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
            fileName = ""
        )
    }
}

private fun getFilteredShoppings(shoppings: List<Api15ShoppingEntity>): List<Api15ShoppingEntity> {
    return shoppings.filterNot { it.deleted }.map {
        it.copy(reminder = 0L)
    }
}

private fun getFilteredProducts(
    shoppings: List<Api15ShoppingEntity>,
    products: List<Api15ProductEntity>
): List<Api15ProductEntity> {
    val shoppingUids = shoppings
        .filter { it.deleted }
        .map { it.uid }
    return products.filterNot { shoppingUids.contains(it.shoppingUid) }
}