package ru.sokolovromann.myshopping.core.data.old

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.data.datasource.LocalBase64
import ru.sokolovromann.myshopping.core.data.datasource.LocalFileManager
import ru.sokolovromann.myshopping.core.data.datasource.LocalJson
import ru.sokolovromann.myshopping.core.data.old.datasource.Api15Dao
import ru.sokolovromann.myshopping.core.data.old.model.Api15AutocompleteEntity
import ru.sokolovromann.myshopping.core.data.old.model.Api15ProductEntity
import ru.sokolovromann.myshopping.core.data.old.model.Api15ShoppingEntity
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.repository.Api15BackupManager

class Api15BackupManagerImpl @Inject constructor(
    private val fileManager: LocalFileManager,
    private val localJson: LocalJson,
    private val localBase64: LocalBase64,
    private val api15Dao: Api15Dao,
    private val roomMigrationManager: Api15RoomMigrationManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : Api15BackupManager {

    private data class Api15BackupValue(
        val shoppings: List<Api15ShoppingEntity>,
        val products: List<Api15ProductEntity>,
        val autocompletes: List<Api15AutocompleteEntity>
    )

    override suspend fun importBackup(uriString: String): Boolean =
        withContext(ioDispatcher) {
            val lines = fileManager.readFile(uriString) ?: return@withContext false
            val backupValue = decodeFromJsonBase64(lines) ?: return@withContext false

            insertToOldDatabase(backupValue)
            migrateToNewDatabase()
            true
        }

    private suspend fun insertToOldDatabase(backupValue: Api15BackupValue) =
        withContext(ioDispatcher) {
            api15Dao.apply {
                val newShoppings = backupValue.shoppings.map { it.copy(reminder = 0L) }
                insertShoppings(newShoppings)

                insertProducts(backupValue.products)
                insertAutocompletes(backupValue.autocompletes)
            }
        }

    private suspend fun migrateToNewDatabase() =
        withContext(ioDispatcher) {
            roomMigrationManager.migrate(ignoreApi = true)
        }

    private suspend fun decodeFromJsonBase64(content: String): Api15BackupValue? =
        withContext(ioDispatcher) {
            val packageNamePrefix = "ru.sokolovromann.myshopping"
            val shoppingPrefix = "$packageNamePrefix.BACKUP_SHOPPING_PREFIX:"
            val productPrefix = "$packageNamePrefix.BACKUP_PRODUCT_PREFIX:"
            val autocompletePrefix = "$packageNamePrefix.BACKUP_AUTOCOMPLETE_PREFIX:"

            val dataList = localBase64.decode(content).split("\n")
            if (!dataList[0].startsWith(packageNamePrefix)) {
                return@withContext null
            }

            val shoppingEntities = mutableListOf<Api15ShoppingEntity>()
            val productEntities = mutableListOf<Api15ProductEntity>()
            val autocompleteEntities = mutableListOf<Api15AutocompleteEntity>()
            dataList.forEach { data ->
                if (data.startsWith(shoppingPrefix)) {
                    val json = data.replace(shoppingPrefix, "")
                    val entity = localJson.decodeFromString<Api15ShoppingEntity>(json)
                    shoppingEntities.add(entity)
                }

                if (data.startsWith(productPrefix)) {
                    val json = data.replace(productPrefix, "")
                    val entity = localJson.decodeFromString<Api15ProductEntity>(json)
                    productEntities.add(entity)
                }

                if (data.startsWith(autocompletePrefix)) {
                    val json = data.replace(autocompletePrefix, "")
                    val entity = localJson.decodeFromString<Api15AutocompleteEntity>(json)
                    autocompleteEntities.add(entity)
                }
            }
            Api15BackupValue(
                shoppings = shoppingEntities,
                products = productEntities,
                autocompletes = autocompleteEntities
            )
        }
}