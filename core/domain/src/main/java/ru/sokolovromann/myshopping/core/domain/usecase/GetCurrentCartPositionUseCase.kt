package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.model.Position
import ru.sokolovromann.myshopping.core.domain.repository.CartsRepository

class GetCurrentCartPositionUseCase @Inject constructor(
    private val cartsRepository: CartsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend operator fun invoke(): Position? = withContext(ioDispatcher) {
        cartsRepository.getCurrentCartPosition()
    }
}