package ru.sokolovromann.myshopping.data.local.files

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Files
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.AppJson
import ru.sokolovromann.myshopping.data.local.entity.AppPreferencesEntity
import ru.sokolovromann.myshopping.data.local.entity.AutocompleteEntity
import ru.sokolovromann.myshopping.data.local.entity.BackupFileEntity
import ru.sokolovromann.myshopping.data.local.entity.ProductEntity
import ru.sokolovromann.myshopping.data.local.entity.ShoppingEntity
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject

class BackupFiles @Inject constructor(
    private val context: Context,
    private val json: AppJson,
    private val dispatchers: AppDispatchers
) {

    private val relativePath = "${Environment.DIRECTORY_DOCUMENTS}/MyShopping"
    private val pathname = "${Environment.getExternalStorageDirectory()}/$relativePath"

    private val appVersionPrefix = "ru.sokolovromann.myshopping.APP_VERSION:"
    private val shoppingPrefix = "ru.sokolovromann.myshopping.BACKUP_SHOPPING_PREFIX_"
    private val productPrefix = "ru.sokolovromann.myshopping.BACKUP_PRODUCT_PREFIX_"
    private val autocompletePrefix = "ru.sokolovromann.myshopping.BACKUP_AUTOCOMPLETE_PREFIX_"
    private val preferencesPrefix = "ru.sokolovromann.myshopping.BACKUP_PREFERENCES_PREFIX_"

    suspend fun writeBackup(entity: BackupFileEntity): Result<String> = withContext(dispatchers.io) {
        return@withContext try {
            val entityJson = encodeBackupFileEntity(entity)
            val displayName = createDisplayName()
            val path = "/$relativePath/$displayName"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
                }

                val contentResolver = context.contentResolver
                contentResolver.insert(Files.getContentUri("external"), values)?.let {
                    contentResolver.openOutputStream(it)?.apply {
                        write(entityJson.toByteArray())
                    }
                }
                Result.success(path)
            } else {
                val file = File("$pathname/$displayName")
                val parentFile = file.parentFile
                if (parentFile?.exists() == true) {
                    file.writeText(entityJson)
                    Result.success(path)
                } else {
                    if (parentFile?.mkdirs() == true) {
                        file.writeText(entityJson)
                        Result.success(path)
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

    suspend fun readBackup(uri: Uri): Result<Flow<BackupFileEntity>> = withContext(dispatchers.io) {
        return@withContext try {
            val contentResolver = context.contentResolver

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

    private fun encodeBackupFileEntity(entity: BackupFileEntity): String {
        val jsons = mutableListOf<String>().apply {
            val shoppingListJsons = encodeShoppingEntities(entity.shoppingEntities)
            addAll(shoppingListJsons)

            val productJsons = encodeProductEntities(entity.productEntities)
            addAll(productJsons)

            val autocompletesJsons = encodeAutocompleteEntities(entity.autocompleteEntities)
            addAll(autocompletesJsons)

            val preferencesJson = encodePreferencesEntity(entity.preferencesEntity)
            add(preferencesJson)
        }

        var jsonsText = "$appVersionPrefix${entity.appVersion}\n"
        jsons.forEach { text -> jsonsText += "$text\n" }

        return jsonsText.dropLast(1)
    }

    private fun encodeShoppingEntities(entities: List<ShoppingEntity>): List<String> {
        return entities.map { "$shoppingPrefix${json.encodeToString(it)}" }
    }

    private fun encodeProductEntities(entities: List<ProductEntity>): List<String> {
        return entities.map { "$productPrefix${json.encodeToString(it)}" }
    }

    private fun encodeAutocompleteEntities(entities: List<AutocompleteEntity>): List<String> {
        return entities.map { "$autocompletePrefix${json.encodeToString(it)}" }
    }

    private fun encodePreferencesEntity(entity: AppPreferencesEntity): String {
        return "$preferencesPrefix${json.encodeToString(entity)}"
    }

    private fun decodeBackupFileEntity(inputStream: InputStream): BackupFileEntity {
        var appVersion = 0
        val shoppingEntities = mutableListOf<ShoppingEntity>()
        val productEntities = mutableListOf<ProductEntity>()
        val autocompleteEntities = mutableListOf<AutocompleteEntity>()
        var appPreferencesEntity = AppPreferencesEntity()

        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var line: String? = reader.readLine()
            while (line != null) {
                if (line.contains(appVersionPrefix)) {
                    appVersion = line.replace(appVersionPrefix, "").toIntOrNull() ?: 0
                }

                if (line.contains(shoppingPrefix)) {
                    val shoppingEntity = decodeShoppingEntity(line)
                    shoppingEntities.add(shoppingEntity)
                }

                if (line.contains(productPrefix)) {
                    val productEntity = decodeProductEntity(line)
                    productEntities.add(productEntity)
                }

                if (line.contains(autocompletePrefix)) {
                    val autocompleteEntity = decodeAutocompleteEntity(line)
                    autocompleteEntities.add(autocompleteEntity)
                }

                if (line.contains(preferencesPrefix)) {
                    val preferencesEntity = decodePreferencesEntity(line)
                    appPreferencesEntity = preferencesEntity
                }

                line = reader.readLine()
            }
        }

        return BackupFileEntity(
            shoppingEntities = shoppingEntities,
            productEntities = productEntities,
            autocompleteEntities = autocompleteEntities,
            preferencesEntity = appPreferencesEntity,
            appVersion = appVersion
        )
    }

    private fun decodeShoppingEntity(value: String): ShoppingEntity {
        val entityJson = value.replace(shoppingPrefix, "")
        return json.decodeFromString(entityJson)
    }

    private fun decodeProductEntity(value: String): ProductEntity {
        val entityJson = value.replace(productPrefix, "")
        return json.decodeFromString(entityJson)
    }

    private fun decodeAutocompleteEntity(value: String): AutocompleteEntity {
        val entityJson = value.replace(autocompletePrefix, "")
        return json.decodeFromString(entityJson)
    }

    private fun decodePreferencesEntity(value: String): AppPreferencesEntity {
        val entityJson = value.replace(preferencesPrefix, "")
        return json.decodeFromString(entityJson)
    }

    private fun createDisplayName(): String {
        val currentTime = System.currentTimeMillis()
        val extension = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) "" else ".txt"
        return "MyShoppingBackup_$currentTime$extension"
    }
}