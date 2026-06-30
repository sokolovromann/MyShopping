package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.sokolovromann.myshopping.core.domain.model.ProductsWidgetPreferences
import ru.sokolovromann.myshopping.core.domain.repository.ProductsWidgetPreferencesRepository

class ObserveProductsWidgetPreferencesUseCase @Inject constructor(
    private val productsWidgetPreferencesRepository: ProductsWidgetPreferencesRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    operator fun invoke(): Flow<ProductsWidgetPreferences> =
        productsWidgetPreferencesRepository.observeProductsWidgetPreferences().flowOn(ioDispatcher)
}