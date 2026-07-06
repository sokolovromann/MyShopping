package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.FabricDirectory
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.repository.FabricsRepository

class DeleteFabricsUseCase @Inject constructor(
    private val fabricsRepository: FabricsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(directory: FabricDirectory): Unit = withContext(ioDispatcher) {
        fabricsRepository.deleteFabrics(directory)
    }

    suspend operator fun invoke(uids: Collection<UID>): Unit = withContext(ioDispatcher) {
        fabricsRepository.deleteFabrics(uids)
    }
}