package ru.sokolovromann.myshopping.data.local.dao

import android.net.Uri
import ru.sokolovromann.myshopping.data.local.entity.AutocompleteEntity
import ru.sokolovromann.myshopping.data.local.entity.BackupFileEntity
import ru.sokolovromann.myshopping.data.local.entity.ProductEntity
import ru.sokolovromann.myshopping.data.local.entity.ShoppingEntity
import ru.sokolovromann.myshopping.data.model.DateTime
import ru.sokolovromann.myshopping.io.LocalBase64
import ru.sokolovromann.myshopping.io.LocalEnvironment
import ru.sokolovromann.myshopping.io.LocalFile
import ru.sokolovromann.myshopping.io.LocalJson
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext
import javax.inject.Inject

class BackupDao @Inject constructor(
    private val localFile: LocalFile,
    private val localJson: LocalJson,
    private val localBase64: LocalBase64
) {
    private val dispatcher = Dispatcher.IO

    private val packageNamePrefix = "ru.sokolovromann.myshopping"
    private val codeVersionPrefix = "$packageNamePrefix.CODE_VERSION:"
    private val shoppingPrefix = "$packageNamePrefix.BACKUP_SHOPPING_PREFIX:"
    private val productPrefix = "$packageNamePrefix.BACKUP_PRODUCT_PREFIX:"
    private val autocompletePrefix = "$packageNamePrefix.BACKUP_AUTOCOMPLETE_PREFIX:"

    suspend fun writeBackup(entity: BackupFileEntity): String? = withContext(dispatcher) {
        val displayName = createDisplayName()
        localFile.writeFile(
            path = LocalEnvironment.createFilePath(displayName, LocalEnvironment.ABSOLUTE_OLD_ROOT_DIRECTORY),
            text = encodeBackupFileEntity(entity)
        )
        return@withContext LocalEnvironment.createFilePath(displayName, LocalEnvironment.RELATIVE_OLD_ROOT_DIRECTORY)
    }

    suspend fun readBackup(uri: Uri): BackupFileEntity? = withContext(dispatcher) {
        val texts = localFile.readLine(uri) ?: return@withContext null
        decodeBackupFileEntity(texts)
    }

    private suspend fun encodeBackupFileEntity(entity: BackupFileEntity): String {
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

        return localBase64.encode(jsonsText.dropLast(1))
    }

    private fun encodeShoppingEntities(entities: List<ShoppingEntity>): List<String> {
        return entities.map { "$shoppingPrefix${localJson.encodeToString(it)}" }
    }

    private fun encodeProductEntities(entities: List<ProductEntity>): List<String> {
        return entities.map { "$productPrefix${localJson.encodeToString(it)}" }
    }

    private fun encodeAutocompleteEntities(entities: List<AutocompleteEntity>): List<String> {
        return entities.map { "$autocompletePrefix${localJson.encodeToString(it)}" }
    }

    private suspend fun decodeBackupFileEntity(text: String): BackupFileEntity? {
        val dataList = localBase64.decode(text).split("\n")
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
        return localJson.decodeFromString(entityJson)
    }

    private fun decodeProductEntity(value: String): ProductEntity {
        val entityJson = value.replace(productPrefix, "")
        return localJson.decodeFromString(entityJson)
    }

    private fun decodeAutocompleteEntity(value: String): AutocompleteEntity {
        val entityJson = value.replace(autocompletePrefix, "")
        return localJson.decodeFromString(entityJson)
    }

    private fun createDisplayName(): String {
        val formattedMillis = DateTime.getCurrentDateTime().getFormattedMillis()
        return "Backup_$formattedMillis.txt"
    }
}