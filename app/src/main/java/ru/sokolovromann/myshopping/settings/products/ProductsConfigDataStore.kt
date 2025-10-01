package ru.sokolovromann.myshopping.settings.products

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.flowOn
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext
import javax.inject.Inject

class ProductsConfigDataStore @Inject constructor(
    private val context: Context,
    private val mapper: ProductsConfigMapper,
    private val dispatcher: Dispatcher
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = ProductsConfigScheme.DATA_STORE_NAME
    )

    fun observe(): Flow<ProductsConfig> {
        return context.dataStore.data
            .map { mapper.mapTo(it) }
            .flowOn(dispatcher)
    }

    suspend fun update(productsConfig: ProductsConfig): Unit = withContext(dispatcher) {
        context.dataStore.edit {
            val newPreferences = mapper.mapFrom(productsConfig)
            it.plusAssign(newPreferences)
        }
    }
}