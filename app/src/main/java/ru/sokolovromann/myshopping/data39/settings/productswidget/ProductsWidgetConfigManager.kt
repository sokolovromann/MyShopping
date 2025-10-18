package ru.sokolovromann.myshopping.data39.settings.productswidget

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.sokolovromann.myshopping.data39.settings.general.FontSize
import ru.sokolovromann.myshopping.data39.settings.general.Theme
import ru.sokolovromann.myshopping.data39.settings.products.GroupProducts
import ru.sokolovromann.myshopping.data39.settings.products.SortProducts
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.flowOnIo
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withIoContext
import javax.inject.Inject

class ProductsWidgetConfigManager @Inject constructor(
    private val context: Context,
    private val mapper: ProductsWidgetConfigMapper
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = ProductsWidgetConfigScheme.DATA_STORE_NAME
    )

    fun observeConfig(): Flow<ProductsWidgetConfig> {
        return context.dataStore.data
            .map { mapper.mapEntityTo(it) }
            .flowOnIo()
    }

    suspend fun getConfig(): ProductsWidgetConfig = withIoContext {
        return@withIoContext context.dataStore.data
            .map { mapper.mapEntityTo(it) }
            .first()
    }

    suspend fun updateConfig(config: ProductsWidgetConfig): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapEntityFrom(config)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateTheme(theme: Theme): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapThemeFrom(theme)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateFontSize(fontSize: FontSize): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapFontSizeFrom(fontSize)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateSortProducts(sort: SortProducts): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapSortFrom(sort)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateGroupProducts(group: GroupProducts): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapGroupFrom(group)
            it.plusAssign(newPreferences)
        }
    }
}