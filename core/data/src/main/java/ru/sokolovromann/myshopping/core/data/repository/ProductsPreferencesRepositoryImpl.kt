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
import ru.sokolovromann.myshopping.core.data.di.ProductsPreferencesDataStore
import ru.sokolovromann.myshopping.core.data.mapper.ProductsPreferencesMapper
import ru.sokolovromann.myshopping.core.domain.model.ProductsPreferences
import ru.sokolovromann.myshopping.core.domain.repository.ProductsPreferencesRepository

class ProductsPreferencesRepositoryImpl @Inject constructor(
    @ProductsPreferencesDataStore private val productsPreferencesDataStore: DataStore<Preferences>,
    private val productsPreferencesMapper: ProductsPreferencesMapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProductsPreferencesRepository {

    override fun observeProductsPreferences(): Flow<ProductsPreferences> =
        productsPreferencesDataStore.data
            .map { productsPreferencesMapper.toModel(it) }
            .flowOn(ioDispatcher)

    override suspend fun updateProductsPreferences(preferences: ProductsPreferences): Unit =
        withContext(ioDispatcher) {
            productsPreferencesDataStore.edit {
                val newPreferences = productsPreferencesMapper.toPreferences(preferences)
                it.plusAssign(newPreferences)
            }
        }
}