package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.Fabric
import ru.sokolovromann.myshopping.core.domain.repository.FabricsRepository

class InsertFabricsUseCase @Inject constructor(
    private val fabricsRepository: FabricsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(fabrics: Collection<Fabric>): Unit =
        withContext(ioDispatcher) {
            fabricsRepository.insertFabrics(fabrics)
        }

    suspend operator fun invoke(fabric: Fabric): Unit =
        withContext(ioDispatcher) {
            val fabrics = listOf(fabric)
            fabricsRepository.insertFabrics(fabrics)
        }
}