package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.Position
import ru.sokolovromann.myshopping.core.domain.repository.CartsRepository

class GetCurrentCartPositionUseCase @Inject constructor(
    private val cartsRepository: CartsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(): Position? =
        withContext(ioDispatcher) {
            cartsRepository.getCurrentCartPosition()
        }
}