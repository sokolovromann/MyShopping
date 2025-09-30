package ru.sokolovromann.myshopping.settings.general

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

class GeneralConfigDataStore @Inject constructor(
    private val context: Context,
    private val mapper: GeneralConfigMapper,
    private val dispatcher: Dispatcher
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = GeneralConfigScheme.DATA_STORE_NAME
    )

    fun observe(): Flow<GeneralConfig> {
        return context.dataStore.data
            .map { mapper.mapTo(it) }
            .flowOn(dispatcher)
    }

    suspend fun update(generalConfig: GeneralConfig): Unit = withContext(dispatcher) {
        context.dataStore.edit {
            val newPreferences = mapper.mapFrom(generalConfig)
            it.plusAssign(newPreferences)
        }
    }
}