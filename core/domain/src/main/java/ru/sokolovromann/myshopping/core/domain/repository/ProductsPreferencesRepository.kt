package ru.sokolovromann.myshopping.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.core.domain.model.ProductsPreferences

interface ProductsPreferencesRepository {

    fun observeProductsPreferences(): Flow<ProductsPreferences>

    suspend fun updateProductsPreferences(preferences: ProductsPreferences)
}