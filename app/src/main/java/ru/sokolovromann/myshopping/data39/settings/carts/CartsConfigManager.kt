package ru.sokolovromann.myshopping.data39.settings.carts

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

class CartsConfigManager @Inject constructor(
    private val context: Context,
    private val mapper: CartsConfigMapper
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = CartsConfigScheme.DATA_STORE_NAME
    )

    fun observeConfig(): Flow<CartsConfig> {
        return context.dataStore.data
            .map { mapper.mapEntityTo(it) }
            .flowOnIo()
    }

    suspend fun updateConfig(config: CartsConfig): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapEntityFrom(config)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateViewMode(viewMode: CartsViewMode): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapViewModeFrom(viewMode)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateSort(sort: SortCarts): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapSortFrom(sort)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateGroup(group: GroupCarts): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapGroupFrom(group)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateCalculateTotal(calculateTotal: CalculateCartsTotal): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapCalculateTotalFrom(calculateTotal)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateAfterAdding(afterAdding: AfterAddingCart): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapAfterAddingFrom(afterAdding)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateAfterCompleting(afterCompleting: AfterCompletingCart): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapAfterCompletingFrom(afterCompleting)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateAfterArchiving(afterArchiving: AfterArchivingCart): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapAfterArchivingFrom(afterArchiving)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateAfterTappingByCheckbox(afterTappingByCheckbox: AfterTappingByCartCheckbox): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapAfterTappingByCheckboxFrom(afterTappingByCheckbox)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateCheckboxesColor(checkboxesColor: CartsCheckboxesColor): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapCheckboxesColorFrom(checkboxesColor)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateSwipeLeft(swipeCart: SwipeCart): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapSwipeLeftFrom(swipeCart)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateSwipeRight(swipeCart: SwipeCart): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapSwipeRightFrom(swipeCart)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateEmptyCarts(emptyCarts: EmptyCarts): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapEmptyCartsFrom(emptyCarts)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateDeletionFromTrash(deletionFromTrash: DeletionCartsFromTrash): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapDeletionFromTrashFrom(deletionFromTrash)
            it.plusAssign(newPreferences)
        }
    }
}