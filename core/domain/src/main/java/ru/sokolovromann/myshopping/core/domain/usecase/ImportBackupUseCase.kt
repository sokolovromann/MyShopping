package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.repository.Api15BackupManager
import ru.sokolovromann.myshopping.core.domain.repository.BackupRepository
import ru.sokolovromann.myshopping.core.domain.repository.CartsRepository
import ru.sokolovromann.myshopping.core.domain.repository.FabricsRepository
import ru.sokolovromann.myshopping.core.domain.repository.ProductsRepository
import ru.sokolovromann.myshopping.core.domain.repository.SuggestionsRepository

class ImportBackupUseCase @Inject constructor(
    private val backupRepository: BackupRepository,
    private val api15BackupManager: Api15BackupManager,
    private val cartsRepository: CartsRepository,
    private val productsRepository: ProductsRepository,
    private val suggestionsRepository: SuggestionsRepository,
    private val fabricsRepository: FabricsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(uriString: String): Boolean =
        withContext(ioDispatcher) {
            val api15Result = api15BackupManager.importBackup(uriString)
            if (api15Result) return@withContext true

            val backupValue = backupRepository.importBackup(uriString)
            if (backupValue != null) {
                cartsRepository.apply {
                    clearCarts()
                    insertCarts(backupValue.carts)
                }
                productsRepository.apply {
                    clearProducts()
                    insertProducts(backupValue.products)
                }
                suggestionsRepository.apply {
                    clearSuggestions()
                    insertSuggestions(backupValue.suggestions)
                }
                fabricsRepository.apply {
                    clearFabrics()
                    insertFabrics(backupValue.fabrics)
                }
                true
            } else false
        }
}