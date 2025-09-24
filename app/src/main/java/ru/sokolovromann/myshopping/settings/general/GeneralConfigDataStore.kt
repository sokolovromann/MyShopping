package ru.sokolovromann.myshopping.settings.general

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.sokolovromann.myshopping.MyShoppingContext
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.flowOn
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeneralConfigDataStore @Inject constructor(
    private val myShoppingContext: MyShoppingContext,
    private val mapper: GeneralConfigMapper
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = GeneralConfigScheme.DATA_STORE_NAME
    )

    private val dispatcher: Dispatcher = Dispatcher.IO

    fun observe(): Flow<GeneralConfig> {
        return myShoppingContext.context.dataStore.data
            .map { mapper.mapTo(it) }
            .flowOn(dispatcher)
    }

    suspend fun update(generalConfig: GeneralConfig): Unit = withContext(dispatcher) {
        myShoppingContext.context.dataStore.edit {
            val newPreferences = mapper.mapFrom(generalConfig)
            it.plusAssign(newPreferences)
        }
    }
}