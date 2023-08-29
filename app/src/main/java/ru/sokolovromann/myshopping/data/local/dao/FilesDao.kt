package ru.sokolovromann.myshopping.data.local.dao

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppBase64
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.app.AppJson
import ru.sokolovromann.myshopping.data.local.datasource.AppContent
import ru.sokolovromann.myshopping.data.local.entity.AppConfigEntity
import ru.sokolovromann.myshopping.data.local.entity.AutocompleteEntity
import ru.sokolovromann.myshopping.data.local.entity.BackupFileEntity
import ru.sokolovromann.myshopping.data.local.entity.ProductEntity
import ru.sokolovromann.myshopping.data.local.entity.ShoppingEntity
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.InputStreamReader

class FilesDao(appContent: AppContent) {

    private val contentResolver = appContent.getContentResolver()

    private val packageNamePrefix = "ru.sokolovromann.myshopping"
    private val codeVersionPrefix = "ru.sokolovromann.myshopping.CODE_VERSION:"
    private val shoppingPrefix = "ru.sokolovromann.myshopping.BACKUP_SHOPPING_PREFIX:"
    private val productPrefix = "ru.sokolovromann.myshopping.BACKUP_PRODUCT_PREFIX:"
    private val autocompletePrefix = "ru.sokolovromann.myshopping.BACKUP_AUTOCOMPLETE_PREFIX:"
    private val appConfigPrefix = "ru.sokolovromann.myshopping.BACKUP_APP_CONFIG_PREFIX:"

    suspend fun writeBackup(entity: BackupFileEntity): Result<String> = withContext(AppDispatchers.IO) {
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

    suspend fun readBackup(uri: Uri): Result<Flow<BackupFileEntity>> = withContext(AppDispatchers.IO) {
        return@withContext try {
            var backupFileEntity = BackupFileEntity()
            contentResolver.openInputStream(uri)?.use { inputStream ->
                backupFileEntity = decodeBackupFileEntity(inputStream)
            }

            val flow = flowOf(backupFileEntity)
            Result.success(flow)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkFile(uri: Uri): Result<Unit> = withContext(AppDispatchers.IO) {
        return@withContext try {
            var correctPackageName = false
            contentResolver.openInputStream(uri)?.use { inputStream ->
                correctPackageName = decodePackageName(inputStream)
            }

            if (correctPackageName) {
                Result.success(Unit)
            } else {
                Result.failure(FileNotFoundException())
            }
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

            val appConfigJson = encodeAppConfigEntity(entity.appConfigEntity)
            add(appConfigJson)
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

    private fun encodeAppConfigEntity(entity: AppConfigEntity): String {
        return "$appConfigPrefix${AppJson.encodeToString(entity)}"
    }

    private fun decodePackageName(inputStream: InputStream): Boolean {
        var correctPackageName = false
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            val line: String = reader.readLine() ?: return@use
            val codeVersionWithPackageName = AppBase64.decode(line).split("\n")[0]
            correctPackageName = codeVersionWithPackageName.startsWith(packageNamePrefix)
        }

        return correctPackageName
    }

    private fun decodeBackupFileEntity(inputStream: InputStream): BackupFileEntity {
        var appVersion = 0
        val shoppingEntities = mutableListOf<ShoppingEntity>()
        val productEntities = mutableListOf<ProductEntity>()
        val autocompleteEntities = mutableListOf<AutocompleteEntity>()
        var appConfigEntity = AppConfigEntity()

        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            val line: String = reader.readLine() ?: return@use
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

                if (it.startsWith(appConfigPrefix)) {
                    val appConfig = decodeAppConfigEntity(it)
                    appConfigEntity = appConfig
                }
            }
        }

        return BackupFileEntity(
            shoppingEntities = shoppingEntities,
            productEntities = productEntities,
            autocompleteEntities = autocompleteEntities,
            appConfigEntity = appConfigEntity,
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

    private fun decodeAppConfigEntity(value: String): AppConfigEntity {
        val entityJson = value.replace(appConfigPrefix, "")
        return AppJson.decodeFromString(entityJson)
    }

    private fun createDisplayName(): String {
        val currentTime = System.currentTimeMillis()
        val currentTimeFormat= String.format(
            "%tY%tm%td_%tH%tM%tS",
            currentTime, currentTime, currentTime, currentTime, currentTime, currentTime
        )

        val extension = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) "" else ".txt"
        return "Backup_$currentTimeFormat$extension"
    }
}