package ru.sokolovromann.myshopping.data.local.files

import android.content.ContentValues
import android.content.Context
import android.net.Uri
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
import java.io.File
import javax.inject.Inject

class BackupFiles @Inject constructor(
    private val context: Context,
    private val json: AppJson,
    private val dispatchers: AppDispatchers
) {

    private val relativePath = "${Environment.DIRECTORY_DOCUMENTS}/MyShopping"

    private val shoppingPrefix = "ru.sokolovromann.myshopping.BACKUP_SHOPPING_PREFIX_"
    private val productPrefix = "ru.sokolovromann.myshopping.BACKUP_PRODUCT_PREFIX_"
    private val autocompletePrefix = "ru.sokolovromann.myshopping.BACKUP_AUTOCOMPLETE_PREFIX_"
    private val preferencesPrefix = "ru.sokolovromann.myshopping.BACKUP_PREFERENCES_PREFIX_"

    suspend fun writeBackup(entity: BackupFileEntity): Result<String> = withContext(dispatchers.io) {
        return@withContext try {
            val displayName = createDisplayName()
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
            }

            val contentResolver = context.contentResolver
            contentResolver.insert(Files.getContentUri("external"), values)?.let {
                contentResolver.openOutputStream(it)?.apply {
                    val entityJson = encodeBackupFileEntity(entity)
                    write(entityJson.toByteArray())
                }
            }

            val path = "/$relativePath/$displayName"
            Result.success(path)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun readBackup(uri: Uri): Result<Flow<BackupFileEntity>> = withContext(dispatchers.io) {
        return@withContext try {
            val contentResolver = context.contentResolver

            var fileName: String? = null
            contentResolver.query(uri, null, null, null, null)?.let {
                if (it.moveToNext()) {
                    val index = it.getColumnIndexOrThrow(Files.FileColumns.DISPLAY_NAME)
                    val pathUri = Uri.parse(it.getString(index))
                    fileName = "${Environment.getExternalStorageDirectory()}/$relativePath/${pathUri.lastPathSegment}"
                }
                it.close()
            }

            if (fileName == null) {
                Result.failure(NullPointerException("File name must not be null"))
            } else {
                val file = File(fileName.toString())

                val backupFileEntity = decodeBackupFileEntity(file)
                val flow = flowOf(backupFileEntity)
                Result.success(flow)
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

            val preferencesJson = encodePreferencesEntity(entity.preferencesEntity)
            add(preferencesJson)
        }

        var jsonsText = ""
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

    private fun decodeBackupFileEntity(file: File): BackupFileEntity {
        val shoppingEntities = mutableListOf<ShoppingEntity>()
        val productEntities = mutableListOf<ProductEntity>()
        val autocompleteEntities = mutableListOf<AutocompleteEntity>()
        var appPreferencesEntity = AppPreferencesEntity()

        file.forEachLine {
            if (it.contains(shoppingPrefix)) {
                val shoppingEntity = decodeShoppingEntity(it)
                shoppingEntities.add(shoppingEntity)
            }

            if (it.contains(productPrefix)) {
                val productEntity = decodeProductEntity(it)
                productEntities.add(productEntity)
            }

            if (it.contains(autocompletePrefix)) {
                val autocompleteEntity = decodeAutocompleteEntity(it)
                autocompleteEntities.add(autocompleteEntity)
            }

            if (it.contains(preferencesPrefix)) {
                val preferencesEntity = decodePreferencesEntity(it)
                appPreferencesEntity = preferencesEntity
            }
        }

        return BackupFileEntity(
            shoppingEntities = shoppingEntities,
            productEntities = productEntities,
            autocompleteEntities = autocompleteEntities,
            preferencesEntity = appPreferencesEntity
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
        return "MyShoppingBackup_$currentTime"
    }
}