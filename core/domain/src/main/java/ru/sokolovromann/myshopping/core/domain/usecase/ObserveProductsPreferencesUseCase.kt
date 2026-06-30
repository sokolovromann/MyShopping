package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.sokolovromann.myshopping.core.domain.model.ProductsPreferences
import ru.sokolovromann.myshopping.core.domain.repository.ProductsPreferencesRepository

class ObserveProductsPreferencesUseCase @Inject constructor(
    private val productsPreferencesRepository: ProductsPreferencesRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    operator fun invoke(): Flow<ProductsPreferences> =
        productsPreferencesRepository.observeProductsPreferences().flowOn(ioDispatcher)
}