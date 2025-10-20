package ru.sokolovromann.myshopping.data39.settings.suggestions

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

class SuggestionsConfigManager @Inject constructor(
    private val context: Context,
    private val mapper: SuggestionsConfigMapper
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = SuggestionsConfigScheme.DATA_STORE_NAME
    )

    fun observeConfig(): Flow<SuggestionsConfig> {
        return context.dataStore.data
            .map { mapper.mapEntityTo(it) }
            .flowOnIo()
    }

    suspend fun getConfig(): SuggestionsConfig = withIoContext {
        return@withIoContext context.dataStore.data
            .map { mapper.mapEntityTo(it) }
            .first()
    }

    suspend fun updateConfig(config: SuggestionsConfig): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapEntityFrom(config)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateViewMode(viewMode: SuggestionsViewMode): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapViewModeFrom(viewMode)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateSort(sort: SortSuggestions): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapSortFrom(sort)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateTake(take: TakeSuggestions): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapTakeFrom(take)
            it.plusAssign(newPreferences)
        }
    }
}