package ru.sokolovromann.myshopping.data39.settings.user

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

class UserConfigManager @Inject constructor(
    private val context: Context,
    private val mapper: UserConfigMapper
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = UserConfigScheme.DATA_STORE_NAME
    )

    fun observeConfig(): Flow<UserConfig> {
        return context.dataStore.data
            .map { mapper.mapEntityTo(it) }
            .flowOnIo()
    }

    suspend fun getConfig(): UserConfig = withIoContext {
        return@withIoContext context.dataStore.data
            .map { mapper.mapEntityTo(it) }
            .first()
    }

    suspend fun updateConfig(config: UserConfig): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapEntityFrom(config)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateApi(api: String): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapApiFrom(api)
            it.plusAssign(newPreferences)
        }
    }
}