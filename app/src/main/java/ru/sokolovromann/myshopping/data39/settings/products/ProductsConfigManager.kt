package ru.sokolovromann.myshopping.data39.settings.products

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.flowOnIo
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withIoContext
import javax.inject.Inject

class ProductsConfigManager @Inject constructor(
    private val context: Context,
    private val mapper: ProductsConfigMapper
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = ProductsConfigScheme.DATA_STORE_NAME
    )

    fun observeConfig(): Flow<ProductsConfig> {
        return context.dataStore.data
            .map { mapper.mapEntityTo(it) }
            .flowOnIo()
    }

    suspend fun updateConfig(config: ProductsConfig): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapEntityFrom(config)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateViewMode(viewMode: ProductsViewMode): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapViewModeFrom(viewMode)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateSort(sort: SortProducts): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapSortFrom(sort)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateGroup(group: GroupProducts): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapGroupFrom(group)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateAddingMode(addingMode: AddingProductMode): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapAddingModeFrom(addingMode)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateCalculateTotal(calculateTotal: CalculateProductsTotal): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapCalculateTotalFrom(calculateTotal)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateStrikethroughCompleted(strikethroughCompleted: StrikethroughCompletedProducts): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapStrikethroughCompletedFrom(strikethroughCompleted)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateAfterCompleting(afterCompleting: AfterCompletingProduct): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapAfterCompletingFrom(afterCompleting)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateAfterTappingByCheckbox(afterTappingByCheckbox: AfterTappingByProductCheckbox): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapAfterTappingByCheckboxFrom(afterTappingByCheckbox)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateCheckboxesColor(checkboxesColor: ProductsCheckboxesColor): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapCheckboxesColorFrom(checkboxesColor)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateAfterTappingByItem(afterTappingByItem: AfterTappingByProductItem): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapAfterTappingByItemFrom(afterTappingByItem)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateSwipeLeft(swipeLeft: SwipeProduct): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapSwipeLeftFrom(swipeLeft)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateSwipeRight(swipeRight: SwipeProduct): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapSwipeRightFrom(swipeRight)
            it.plusAssign(newPreferences)
        }
    }
}