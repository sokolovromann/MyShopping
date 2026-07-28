package ru.sokolovromann.myshopping.core.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.data.datasource.LocalBase64
import ru.sokolovromann.myshopping.core.data.datasource.LocalFileManager
import ru.sokolovromann.myshopping.core.data.datasource.LocalJson
import ru.sokolovromann.myshopping.core.data.mapper.CartsMapper
import ru.sokolovromann.myshopping.core.data.mapper.FabricsMapper
import ru.sokolovromann.myshopping.core.data.mapper.ProductsMapper
import ru.sokolovromann.myshopping.core.data.mapper.SuggestionsMapper
import ru.sokolovromann.myshopping.core.data.model.CartEntity
import ru.sokolovromann.myshopping.core.data.model.FabricEntity
import ru.sokolovromann.myshopping.core.data.model.ProductEntity
import ru.sokolovromann.myshopping.core.data.model.SuggestionEntity
import ru.sokolovromann.myshopping.core.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.Backup
import ru.sokolovromann.myshopping.core.domain.model.BackupDirectory
import ru.sokolovromann.myshopping.core.domain.model.BackupValue
import ru.sokolovromann.myshopping.core.domain.model.Cart
import ru.sokolovromann.myshopping.core.domain.model.Fabric
import ru.sokolovromann.myshopping.core.domain.model.Product
import ru.sokolovromann.myshopping.core.domain.model.Suggestion
import ru.sokolovromann.myshopping.core.domain.model.TimeInMillis
import ru.sokolovromann.myshopping.core.domain.repository.BackupRepository
import java.util.Locale

class BackupRepositoryImpl(
    private val fileManager: LocalFileManager,
    private val localJson: LocalJson,
    private val localBase64: LocalBase64,
    private val cartsMapper: CartsMapper,
    private val productsMapper: ProductsMapper,
    private val suggestionsMapper: SuggestionsMapper,
    private val fabricsMapper: FabricsMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BackupRepository {

    private val head = "ru.sokolovromann.myshopping.BACKUP"
    private val cartPrefix = "Cart_"
    private val productPrefix = "Product_"
    private val suggestionPrefix = "Suggestion_"
    private val fabricPrefix = "Fabric_"

    override suspend fun exportBackup(backup: Backup): Boolean =
        withContext(ioDispatcher) {
            val builder = StringBuilder()
            builder.appendLine(head)
            builder.append(encodeCarts(backup.value.carts))
            builder.append(encodeProducts(backup.value.products))
            builder.append(encodeSuggestions(backup.value.suggestions))
            builder.append(encodeFabrics(backup.value.fabrics))

            val path = createPath(backup.directory)
            fileManager.writeFile(path, builder.toString())
        }

    override suspend fun importBackup(uriString: String): BackupValue? =
        withContext(ioDispatcher) {
            val content = fileManager.readFile(uriString) ?: return@withContext null
            val lines = localBase64.decode(content).split("\n")
            if (lines.first() == head) {
                val carts = mutableListOf<Cart>()
                val products = mutableListOf<Product>()
                val suggestions = mutableListOf<Suggestion>()
                val fabrics = mutableListOf<Fabric>()
                lines.forEach { line ->
                    if (line.startsWith(cartPrefix)) {
                        carts.add(decodeCart(line))
                    }
                    if (line.startsWith(productPrefix)) {
                        products.add(decodeProduct(line))
                    }
                    if (line.startsWith(suggestionPrefix)) {
                        suggestions.add(decodeSuggestion(line))
                    }
                    if (line.startsWith(fabricPrefix)) {
                        fabrics.add(decodeFabric(line))
                    }
                }
                BackupValue(carts, products, suggestions, fabrics)
            } else null
        }

    private suspend fun encodeCarts(carts: Collection<Cart>): String =
        withContext(ioDispatcher) {
            val builder = StringBuilder()
            builder.appendLine(cartPrefix)
            cartsMapper.toEntities(carts).forEach {
                val json = localJson.encodeToString(it)
                val base64 = localBase64.encode(json)
                builder.append(base64)
            }
            builder.toString()
        }

    private suspend fun encodeProducts(products: Collection<Product>): String =
        withContext(ioDispatcher) {
            val builder = StringBuilder()
            builder.appendLine(productPrefix)
            productsMapper.toEntities(products).forEach {
                val json = localJson.encodeToString(it)
                val base64 = localBase64.encode(json)
                builder.append(base64)
            }
            builder.toString()
        }

    private suspend fun encodeSuggestions(suggestions: Collection<Suggestion>): String =
        withContext(ioDispatcher) {
            val builder = StringBuilder()
            builder.appendLine(suggestionPrefix)
            suggestionsMapper.toEntities(suggestions).forEach {
                val json = localJson.encodeToString(it)
                val base64 = localBase64.encode(json)
                builder.append(base64)
            }
            builder.toString()
        }

    private suspend fun encodeFabrics(fabrics: Collection<Fabric>): String =
        withContext(ioDispatcher) {
            val builder = StringBuilder()
            builder.appendLine(fabricPrefix)
            fabricsMapper.toEntities(fabrics).forEach {
                val json = localJson.encodeToString(it)
                val base64 = localBase64.encode(json)
                builder.append(base64)
            }
            builder.toString()
        }

    private suspend fun decodeCart(line: String): Cart =
        withContext(ioDispatcher) {
            val lineWithoutPrefix = line.replace(cartPrefix, "")
            val entity = localJson.decodeFromString<CartEntity>(lineWithoutPrefix)
            cartsMapper.toModel(entity)
        }

    private suspend fun decodeProduct(line: String): Product =
        withContext(ioDispatcher) {
            val lineWithoutPrefix = line.replace(productPrefix, "")
            val entity = localJson.decodeFromString<ProductEntity>(lineWithoutPrefix)
            productsMapper.toModel(entity)
        }

    private suspend fun decodeSuggestion(line: String): Suggestion =
        withContext(ioDispatcher) {
            val lineWithoutPrefix = line.replace(suggestionPrefix, "")
            val entity = localJson.decodeFromString<SuggestionEntity>(lineWithoutPrefix)
            suggestionsMapper.toModel(entity)
        }

    private suspend fun decodeFabric(line: String): Fabric =
        withContext(ioDispatcher) {
            val lineWithoutPrefix = line.replace(fabricPrefix, "")
            val entity = localJson.decodeFromString<FabricEntity>(lineWithoutPrefix)
            fabricsMapper.toModel(entity)
        }

    private fun createPath(directory: BackupDirectory): String {
        val format = "%tY%tm%td_%tH%tM%tS"
        val millis = mutableListOf<Long>().apply {
            val timeInMillis = TimeInMillis.getCurrent().value
            (1..6).forEach { _ -> add(timeInMillis) }
        }
        val dateTime = String.format(Locale.getDefault(), format, millis)
        return "${directory.value}/Backup_$dateTime"
    }
}