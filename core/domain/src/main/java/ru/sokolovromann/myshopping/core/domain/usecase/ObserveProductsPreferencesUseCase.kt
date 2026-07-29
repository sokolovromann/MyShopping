package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.ProductsPreferences
import ru.sokolovromann.myshopping.core.domain.repository.ProductsPreferencesRepository

class ObserveProductsPreferencesUseCase @Inject constructor(
    private val productsPreferencesRepository: ProductsPreferencesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    operator fun invoke(): Flow<ProductsPreferences> =
        productsPreferencesRepository.observeProductsPreferences().flowOn(ioDispatcher)
}