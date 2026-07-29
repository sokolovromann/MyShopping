package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.CartsPreferences
import ru.sokolovromann.myshopping.core.domain.repository.CartsPreferencesRepository

class ObserveCartsPreferencesUseCase @Inject constructor(
    private val cartsPreferencesRepository: CartsPreferencesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    operator fun invoke(): Flow<CartsPreferences> =
        cartsPreferencesRepository.observeCartsPreferences().flowOn(ioDispatcher)
}