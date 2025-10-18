package ru.sokolovromann.myshopping.data39.settings.autocompletes

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.flowOnIo
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withIoContext
import javax.inject.Inject

class AutocompletesConfigManager @Inject constructor(
    private val context: Context,
    private val mapper: AutocompletesConfigMapper
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = AutocompletesConfigScheme.DATA_STORE_NAME
    )

    fun observeConfig(): Flow<AutocompletesConfig> {
        return context.dataStore.data
            .map { mapper.mapEntityTo(it) }
            .flowOnIo()
    }

    suspend fun getConfig(): AutocompletesConfig = withIoContext {
        return@withIoContext context.dataStore.data
            .map { mapper.mapEntityTo(it) }
            .first()
    }

    suspend fun updateConfig(config: AutocompletesConfig): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapEntityFrom(config)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateViewMode(viewMode: AutocompletesViewMode): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapViewModeFrom(viewMode)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateSort(sort: SortAutocompletes): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapSortFrom(sort)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateMaxNumber(maxNumber: MaxAutocompletesNumber): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapMaxNumberFrom(maxNumber)
            it.plusAssign(newPreferences)
        }
    }
}