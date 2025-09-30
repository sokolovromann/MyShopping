package ru.sokolovromann.myshopping.settings.addeditproduct

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

class AddEditProductConfigDataStore @Inject constructor(
    private val context: Context,
    private val mapper: AddEditProductConfigMapper,
    private val dispatcher: Dispatcher
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = AddEditProductConfigScheme.DATA_STORE_NAME
    )

    fun observe(): Flow<AddEditProductConfig> {
        return context.dataStore.data
            .map { mapper.mapTo(it) }
            .flowOn(dispatcher)
    }

    suspend fun update(addEditProductConfig: AddEditProductConfig): Unit = withContext(dispatcher) {
        context.dataStore.edit {
            val newPreferences = mapper.mapFrom(addEditProductConfig)
            it.plusAssign(newPreferences)
        }
    }
}