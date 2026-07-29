package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.CartDirectory
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.repository.CartsRepository

class DeleteCartsUseCase @Inject constructor(
    private val cartsRepository: CartsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(directory: CartDirectory): Unit =
        withContext(ioDispatcher) {
            cartsRepository.deleteCarts(directory)
        }

    suspend operator fun invoke(uids: Collection<UID>): Unit =
        withContext(ioDispatcher) {
            cartsRepository.deleteCarts(uids)
        }

    suspend operator fun invoke(uid: UID): Unit =
        withContext(ioDispatcher) {
            val uids = listOf(uid)
            cartsRepository.deleteCarts(uids)
        }
}