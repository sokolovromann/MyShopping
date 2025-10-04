package ru.sokolovromann.myshopping.settings.autocompletes

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

class AutocompletesConfigDataStore @Inject constructor(
    private val context: Context,
    private val mapper: AutocompletesConfigMapper,
    private val dispatcher: Dispatcher
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = AutocompletesConfigScheme.DATA_STORE_NAME
    )

    fun observe(): Flow<AutocompletesConfig> {
        return context.dataStore.data
            .map { mapper.mapTo(it) }
            .flowOn(dispatcher)
    }

    suspend fun update(autocompletesConfig: AutocompletesConfig): Unit = withContext(dispatcher) {
        context.dataStore.edit {
            val newPreferences = mapper.mapFrom(autocompletesConfig)
            it.plusAssign(newPreferences)
        }
    }
}