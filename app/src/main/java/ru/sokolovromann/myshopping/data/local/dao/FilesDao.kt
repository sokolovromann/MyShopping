package ru.sokolovromann.myshopping.data.local.dao

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
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
import java.io.FileNotFoundException
import java.io.InputStreamReader

class FilesDao(appContent: AppContent) {

    private val contentResolver = appContent.getContentResolver()
    private val dispatcher = AppDispatchers.IO

    private val packageNamePrefix = "ru.sokolovromann.myshopping"
    private val codeVersionPrefix = "$packageNamePrefix.CODE_VERSION:"
    private val shoppingPrefix = "$packageNamePrefix.BACKUP_SHOPPING_PREFIX:"
    private val productPrefix = "$packageNamePrefix.BACKUP_PRODUCT_PREFIX:"
    private val autocompletePrefix = "$packageNamePrefix.BACKUP_AUTOCOMPLETE_PREFIX:"

    suspend fun writeBackup(entity: BackupFileEntity): Result<String> = withContext(dispatcher) {
        return@withContext try {
            val entityJson = encodeBackupFileEntity(entity)
            val displayName = createDisplayName()

            val extension = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ".txt" else ""
            val resultPath = "/${AppContent.getAppFolderRelativePath()}/$displayName$extension"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, AppContent.getAppFolderRelativePath())
                }

                contentResolver.insert(MediaStore.Files.getContentUri("external"), values)?.let {
                    contentResolver.openOutputStream(it)?.apply {
                        write(entityJson.toByteArray())
                    }
                }
                Result.success(resultPath)
            } else {
                val file = File("${AppContent.getAppFolderAbsolutePath()}/$displayName")
                val parentFile = file.parentFile
                if (parentFile?.exists() == true) {
                    file.writeText(entityJson)
                    Result.success(resultPath)
                } else {
                    if (parentFile?.mkdirs() == true) {
                        file.writeText(entityJson)
                        Result.success(resultPath)
                    } else {
                        val exception = Exception("Parent files not created")
                        Result.failure(exception)
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun readBackup(uri: Uri): Result<BackupFileEntity?> = withContext(dispatcher) {
        return@withContext try {
            var backupFileEntity: BackupFileEntity? = null
            contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    val line: String = reader.readLine() ?: throw FileNotFoundException()
                    if (!decodePackageName(line)) {
                        throw FileNotFoundException()
                    }
                    backupFileEntity = decodeBackupFileEntity(line)
                }
            }

            Result.success(backupFileEntity)
        } catch (e: Exception) {
            Result.failure(e)
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

    private fun decodePackageName(line: String): Boolean {
        val codeVersionWithPackageName = AppBase64.decode(line).split("\n")[0]
        return codeVersionWithPackageName.startsWith(packageNamePrefix)
    }

    private fun decodeBackupFileEntity(line: String): BackupFileEntity {
        var appVersion = 0
        val shoppingEntities = mutableListOf<ShoppingEntity>()
        val productEntities = mutableListOf<ProductEntity>()
        val autocompleteEntities = mutableListOf<AutocompleteEntity>()

        AppBase64.decode(line).split("\n").forEach {
            if (it.startsWith(codeVersionPrefix)) {
                appVersion = it.replace(codeVersionPrefix, "").toIntOrNull() ?: 0
            }

            if (it.startsWith(shoppingPrefix)) {
                val shoppingEntity = decodeShoppingEntity(it)
                shoppingEntities.add(shoppingEntity)
            }

            if (it.startsWith(productPrefix)) {
                val productEntity = decodeProductEntity(it)
                productEntities.add(productEntity)
            }

            if (it.startsWith(autocompletePrefix)) {
                val autocompleteEntity = decodeAutocompleteEntity(it)
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
        val extension = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) "" else ".txt"
        return "Backup_$formattedMillis$extension"
    }
}