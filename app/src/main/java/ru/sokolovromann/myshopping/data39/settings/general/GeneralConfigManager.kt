package ru.sokolovromann.myshopping.data39.settings.general

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

class GeneralConfigManager @Inject constructor(
    private val context: Context,
    private val mapper: GeneralConfigMapper
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = GeneralConfigScheme.DATA_STORE_NAME
    )

    fun observeConfig(): Flow<GeneralConfig> {
        return context.dataStore.data
            .map { mapper.mapEntityTo(it) }
            .flowOnIo()
    }

    suspend fun updateConfig(config: GeneralConfig): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapEntityFrom(config)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateTheme(theme: Theme): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapThemeFrom(theme)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateFontSize(fontSize: FontSize): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapFontSizeFrom(fontSize)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateDateTime(dateTime: DateTimeConfig): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapDateTimeFrom(dateTime)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateMoney(money: MoneyConfig): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapMoneyFrom(money)
            it.plusAssign(newPreferences)
        }
    }
}