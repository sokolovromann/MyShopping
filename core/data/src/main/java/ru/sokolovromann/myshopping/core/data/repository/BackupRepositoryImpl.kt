package ru.sokolovromann.myshopping.core.data.repository

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.data.datasource.LocalBase64
import ru.sokolovromann.myshopping.core.data.datasource.LocalFileManager
import ru.sokolovromann.myshopping.core.data.datasource.LocalJson
import ru.sokolovromann.myshopping.core.data.mapper.CartsMapper
import ru.sokolovromann.myshopping.core.data.mapper.FabricsMapper
import ru.sokolovromann.myshopping.core.data.mapper.ProductsMapper
import ru.sokolovromann.myshopping.core.data.mapper.SuggestionsMapper
import ru.sokolovromann.myshopping.core.data.model.BackupInfoEntity
import ru.sokolovromann.myshopping.core.data.model.Backupable
import ru.sokolovromann.myshopping.core.data.model.CartEntity
import ru.sokolovromann.myshopping.core.data.model.FabricEntity
import ru.sokolovromann.myshopping.core.data.model.ProductEntity
import ru.sokolovromann.myshopping.core.data.model.SuggestionEntity
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.Backup
import ru.sokolovromann.myshopping.core.domain.model.BackupDirectory
import ru.sokolovromann.myshopping.core.domain.model.BackupValue
import ru.sokolovromann.myshopping.core.domain.model.Cart
import ru.sokolovromann.myshopping.core.domain.model.Fabric
import ru.sokolovromann.myshopping.core.domain.model.Product
import ru.sokolovromann.myshopping.core.domain.model.Suggestion
import ru.sokolovromann.myshopping.core.domain.model.TimeInMillis
import ru.sokolovromann.myshopping.core.domain.repository.BackupRepository
import ru.sokolovromann.myshopping.core.domain.repository.BuildInfo
import java.util.Locale

class BackupRepositoryImpl @Inject constructor(
    private val fileManager: LocalFileManager,
    private val localJson: LocalJson,
    private val localBase64: LocalBase64,
    private val cartsMapper: CartsMapper,
    private val productsMapper: ProductsMapper,
    private val suggestionsMapper: SuggestionsMapper,
    private val fabricsMapper: FabricsMapper,
    private val buildInfo: BuildInfo,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BackupRepository {

    override suspend fun exportBackup(backup: Backup): Boolean =
        withContext(ioDispatcher) {
            val path = createPath(backup.directory)
            fileManager.writeFile(path, encodeToJsonBase64(backup.value))
        }

    override suspend fun importBackup(uriString: String): BackupValue? =
        withContext(ioDispatcher) {
            fileManager.readFile(uriString)?.let {
                decodeFromJsonBase64(it)
            }
        }

    private suspend fun encodeToJsonBase64(backupValue: BackupValue): String =
        withContext(ioDispatcher) {
            val data = mutableListOf<Backupable>().apply {
                add(BackupInfoEntity(buildInfo.getApi().value.toString()))
                addAll(cartsMapper.toEntities(backupValue.carts))
                addAll(productsMapper.toEntities(backupValue.products))
                addAll(suggestionsMapper.toEntities(backupValue.suggestions))
                addAll(fabricsMapper.toEntities(backupValue.fabrics))
            }.toList()
            val json = localJson.encodeToString<List<Backupable>>(data)
            localBase64.encode(json)
        }

    private suspend fun decodeFromJsonBase64(content: String): BackupValue =
        withContext(ioDispatcher) {
            val json = localBase64.decode(content)
            val carts = mutableListOf<Cart>()
            val products = mutableListOf<Product>()
            val suggestions = mutableListOf<Suggestion>()
            val fabrics = mutableListOf<Fabric>()

            for (entity in localJson.decodeFromString<List<Backupable>>(json)) {
                when (entity) {
                    is BackupInfoEntity -> {}
                    is CartEntity -> carts.add(cartsMapper.toModel(entity))
                    is ProductEntity -> products.add(productsMapper.toModel(entity))
                    is SuggestionEntity -> suggestions.add(suggestionsMapper.toModel(entity))
                    is FabricEntity -> fabrics.add(fabricsMapper.toModel(entity))
                }
            }
            BackupValue(carts, products, suggestions, fabrics)
        }

    private fun createPath(directory: BackupDirectory): String {
        val format = "%tY%tm%td_%tH%tM%tS"
        val timeInMillis = TimeInMillis.getCurrent().value
        val dateTime = String.format(
            Locale.getDefault(),
            format,
            timeInMillis,
            timeInMillis,
            timeInMillis,
            timeInMillis,
            timeInMillis,
            timeInMillis
        )
        return "${directory.value}/Backup_$dateTime"
    }
}