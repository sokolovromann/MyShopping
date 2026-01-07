package ru.sokolovromann.myshopping.data39.suggestions

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

class SuggestionsConfigRepository @Inject constructor(
    private val context: Context,
    private val configMapper: SuggestionsConfigMapper
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = SuggestionsConfigScheme.DATA_STORE_NAME
    )

    fun observe(): Flow<SuggestionsConfig> {
        return context.dataStore.data
            .map { configMapper.fromPreferences(it) }
            .flowOnIo()
    }

    suspend fun get(): SuggestionsConfig = withIoContext {
        return@withIoContext observe().first()
    }

    suspend fun update(config: SuggestionsConfig): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = configMapper.toPreferences(config)
            it.plusAssign(newPreferences)
        }
    }
}