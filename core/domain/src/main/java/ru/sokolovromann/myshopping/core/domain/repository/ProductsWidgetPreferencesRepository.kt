package ru.sokolovromann.myshopping.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.core.domain.model.ProductsWidgetPreferences

interface ProductsWidgetPreferencesRepository {

    fun observeProductsWidgetPreferences(): Flow<ProductsWidgetPreferences>

    suspend fun updateProductsWidgetPreferences(preferences: ProductsWidgetPreferences)
}