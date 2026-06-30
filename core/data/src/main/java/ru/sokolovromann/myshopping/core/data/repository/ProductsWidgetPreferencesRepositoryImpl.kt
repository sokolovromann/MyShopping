package ru.sokolovromann.myshopping.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.data.di.ProductsWidgetPreferencesDataStore
import ru.sokolovromann.myshopping.core.data.mapper.ProductsWidgetPreferencesMapper
import ru.sokolovromann.myshopping.core.domain.model.ProductsWidgetPreferences
import ru.sokolovromann.myshopping.core.domain.repository.ProductsWidgetPreferencesRepository

class ProductsWidgetPreferencesRepositoryImpl @Inject constructor(
    @ProductsWidgetPreferencesDataStore private val productsWidgetPreferencesDataStore: DataStore<Preferences>,
    private val productsWidgetPreferencesMapper: ProductsWidgetPreferencesMapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProductsWidgetPreferencesRepository {

    override fun observeProductsWidgetPreferences(): Flow<ProductsWidgetPreferences> =
        productsWidgetPreferencesDataStore.data
            .map { productsWidgetPreferencesMapper.toModel(it) }
            .flowOn(ioDispatcher)

    override suspend fun updateProductsWidgetPreferences(preferences: ProductsWidgetPreferences): Unit =
        withContext(ioDispatcher) {
            productsWidgetPreferencesDataStore.edit {
                val newPreferences = productsWidgetPreferencesMapper.toPreferences(preferences)
                it.plusAssign(newPreferences)
            }
        }
}