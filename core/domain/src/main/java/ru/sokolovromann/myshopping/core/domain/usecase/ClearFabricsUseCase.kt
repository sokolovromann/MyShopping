package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.repository.FabricsRepository

class ClearFabricsUseCase @Inject constructor(
    private val fabricsRepository: FabricsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(): Unit =
        withContext(ioDispatcher) {
            fabricsRepository.clearFabrics()
        }
}