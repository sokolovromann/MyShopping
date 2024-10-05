package ru.sokolovromann.myshopping.data.local.dao

import android.net.Uri
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppBase64
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.app.AppJson
import ru.sokolovromann.myshopping.data.local.datasource.AppContent
import ru.sokolovromann.myshopping.data.local.entity.AutocompleteEntity
import ru.sokolovromann.myshopping.data.local.entity.BackupFileEntity
import ru.sokolovromann.myshopping.data.local.entity.ProductEntity
import ru.sokolovromann.myshopping.data.local.entity.ShoppingEntity
import ru.sokolovromann.myshopping.data.model.DateTime
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class BackupDao(appContent: AppContent) {

    private val contentResolver = appContent.getContentResolver()
    private val dispatcher = AppDispatchers.IO

    private val packageNamePrefix = "ru.sokolovromann.myshopping"
    private val codeVersionPrefix = "$packageNamePrefix.CODE_VERSION:"
    private val shoppingPrefix = "$packageNamePrefix.BACKUP_SHOPPING_PREFIX:"
    private val productPrefix = "$packageNamePrefix.BACKUP_PRODUCT_PREFIX:"
    private val autocompletePrefix = "$packageNamePrefix.BACKUP_AUTOCOMPLETE_PREFIX:"

    suspend fun writeBackup(entity: BackupFileEntity): String? = withContext(dispatcher) {
        return@withContext try {
            val displayName = createDisplayName()
            async {
                File(AppContent.getAppFolderAbsolutePath(), displayName).apply {
                    parentFile?.mkdirs()
                    writeText(encodeBackupFileEntity(entity))
                }
            }.await()
            createRelativePath(displayName)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun readBackup(uri: Uri): BackupFileEntity? = withContext(dispatcher) {
        return@withContext try {
            async {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        decodeBackupFileEntity(reader.readLine())
                    }
                }
            }.await()
        } catch (e: Exception) {
            null
        }
    }

    private fun encodeBackupFileEntity(entity: BackupFileEntity): String {
        val jsons = mutableListOf<String>().apply {
            val shoppingListJsons = encodeShoppingEntities(entity.shoppingEntities)
            addAll(shoppingListJsons)

            val productJsons = encodeProductEntities(entity.productEntities)
            addAll(productJsons)

            val autocompletesJsons = encodeAutocompleteEntities(entity.autocompleteEntities)
            addAll(autocompletesJsons)
        }

        var jsonsText = "$codeVersionPrefix${entity.appVersion}\n"
        jsons.forEach { text -> jsonsText += "$text\n" }

        return AppBase64.encode(jsonsText.dropLast(1))
    }

    private fun encodeShoppingEntities(entities: List<ShoppingEntity>): List<String> {
        return entities.map { "$shoppingPrefix${AppJson.encodeToString(it)}" }
    }

    private fun encodeProductEntities(entities: List<ProductEntity>): List<String> {
        return entities.map { "$productPrefix${AppJson.encodeToString(it)}" }
    }

    private fun encodeAutocompleteEntities(entities: List<AutocompleteEntity>): List<String> {
        return entities.map { "$autocompletePrefix${AppJson.encodeToString(it)}" }
    }

    private fun decodeBackupFileEntity(text: String): BackupFileEntity? {
        val dataList = AppBase64.decode(text).split("\n")
        if (!dataList[0].startsWith(packageNamePrefix)) {
            return null
        }

        var appVersion = 0
        val shoppingEntities = mutableListOf<ShoppingEntity>()
        val productEntities = mutableListOf<ProductEntity>()
        val autocompleteEntities = mutableListOf<AutocompleteEntity>()
        dataList.forEach { data ->
            if (data.startsWith(codeVersionPrefix)) {
                appVersion = data.replace(codeVersionPrefix, "").toIntOrNull() ?: 0
            }

            if (data.startsWith(shoppingPrefix)) {
                val shoppingEntity = decodeShoppingEntity(data)
                shoppingEntities.add(shoppingEntity)
            }

            if (data.startsWith(productPrefix)) {
                val productEntity = decodeProductEntity(data)
                productEntities.add(productEntity)
            }

            if (data.startsWith(autocompletePrefix)) {
                val autocompleteEntity = decodeAutocompleteEntity(data)
                autocompleteEntities.add(autocompleteEntity)
            }
        }

        return BackupFileEntity(
            shoppingEntities = shoppingEntities,
            productEntities = productEntities,
            autocompleteEntities = autocompleteEntities,
            appVersion = appVersion
        )
    }

    private fun decodeShoppingEntity(value: String): ShoppingEntity {
        val entityJson = value.replace(shoppingPrefix, "")
        return AppJson.decodeFromString(entityJson)
    }

    private fun decodeProductEntity(value: String): ProductEntity {
        val entityJson = value.replace(productPrefix, "")
        return AppJson.decodeFromString(entityJson)
    }

    private fun decodeAutocompleteEntity(value: String): AutocompleteEntity {
        val entityJson = value.replace(autocompletePrefix, "")
        return AppJson.decodeFromString(entityJson)
    }

    private fun createDisplayName(): String {
        val formattedMillis = DateTime.getCurrentDateTime().getFormattedMillis()
        return "Backup_$formattedMillis.txt"
    }

    private fun createRelativePath(displayName: String): String {
        return "/${AppContent.getAppFolderRelativePath()}/$displayName"
    }
}