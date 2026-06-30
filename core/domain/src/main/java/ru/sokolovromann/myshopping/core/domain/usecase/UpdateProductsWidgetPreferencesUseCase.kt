package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.model.GroupProductsByStatus
import ru.sokolovromann.myshopping.core.domain.model.SortProducts
import ru.sokolovromann.myshopping.core.domain.model.FontSize
import ru.sokolovromann.myshopping.core.domain.model.ProductsWidgetPreferences
import ru.sokolovromann.myshopping.core.domain.model.Theme
import ru.sokolovromann.myshopping.core.domain.repository.ProductsWidgetPreferencesRepository

class UpdateProductsWidgetPreferencesUseCase @Inject constructor(
    private val productsWidgetPreferencesRepository: ProductsWidgetPreferencesRepository,
    private val observeProductsWidgetPreferencesUseCase: ObserveProductsWidgetPreferencesUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend operator fun invoke(preferences: ProductsWidgetPreferences): Unit =
        withContext(ioDispatcher) {
            productsWidgetPreferencesRepository.updateProductsWidgetPreferences(preferences)
        }

    suspend operator fun invoke(theme: Theme): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(theme = theme)
            productsWidgetPreferencesRepository.updateProductsWidgetPreferences(preferences)
        }

    suspend operator fun invoke(fontSize: FontSize): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(fontSize = fontSize)
            productsWidgetPreferencesRepository.updateProductsWidgetPreferences(preferences)
        }

    suspend operator fun invoke(sortProducts: SortProducts): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(sortProducts = sortProducts)
            productsWidgetPreferencesRepository.updateProductsWidgetPreferences(preferences)
        }

    suspend operator fun invoke(groupByStatus: GroupProductsByStatus): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(groupProductsByStatus = groupByStatus)
            productsWidgetPreferencesRepository.updateProductsWidgetPreferences(preferences)
        }

    private suspend fun getPreferences() = observeProductsWidgetPreferencesUseCase().first()
}