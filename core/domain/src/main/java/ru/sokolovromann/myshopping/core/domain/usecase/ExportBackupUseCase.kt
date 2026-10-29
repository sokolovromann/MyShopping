package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.Backup
import ru.sokolovromann.myshopping.core.domain.model.BackupValue
import ru.sokolovromann.myshopping.core.domain.model.CartDirectory
import ru.sokolovromann.myshopping.core.domain.repository.BackupRepository
import ru.sokolovromann.myshopping.core.domain.repository.CartsRepository
import ru.sokolovromann.myshopping.core.domain.repository.SuggestionsRepository

class ExportBackupUseCase @Inject constructor(
    private val backupRepository: BackupRepository,
    private val cartsRepository: CartsRepository,
    private val suggestionsRepository: SuggestionsRepository,
    private val observeBackupPreferencesUseCase: ObserveBackupPreferencesUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(): Boolean =
        withContext(ioDispatcher) {
            val cartsAndProducts = combine(
                flow = cartsRepository.observeCartsWithProducts(CartDirectory.Current),
                flow2 = cartsRepository.observeCartsWithProducts(CartDirectory.Archived),
                transform = { current, archived ->
                    val cartsWithProducts = current.toMutableList().apply { addAll(archived) }
                    val carts = cartsWithProducts.map { it.cart.copy(reminder = null) }
                    val products = cartsWithProducts.flatMap { it.products }
                    Pair(carts, products)
                }
            ).first()
            val suggestionsWithFabrics = suggestionsRepository.observeSuggestionsWithFabrics().first()
            val backupValue = BackupValue(
                carts = cartsAndProducts.first,
                products = cartsAndProducts.second,
                suggestions = suggestionsWithFabrics.map { it.suggestion },
                fabrics = suggestionsWithFabrics.flatMap { it.fabrics }
            )
            val backup = Backup(
                directory = observeBackupPreferencesUseCase().first().directory,
                value = backupValue
            )
            backupRepository.exportBackup(backup)
        }
}