package ru.sokolovromann.myshopping.data39.settings.addeditproduct

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

class AddEditProductConfigManager @Inject constructor(
    private val context: Context,
    private val mapper: AddEditProductConfigMapper
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = AddEditProductConfigScheme.DATA_STORE_NAME
    )

    fun observeConfig(): Flow<AddEditProductConfig> {
        return context.dataStore.data
            .map { mapper.mapEntityTo(it) }
            .flowOnIo()
    }

    suspend fun getConfig(): AddEditProductConfig = withIoContext {
        return@withIoContext context.dataStore.data
            .map { mapper.mapEntityTo(it) }
            .first()
    }

    suspend fun updateConfig(config: AddEditProductConfig): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapEntityFrom(config)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateDisplayFields(displayFields: DisplayAddEditProductFields): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapDisplayFieldsFrom(displayFields)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateLockField(lockField: LockAddEditProductField): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapLockFieldFrom(lockField)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateKeyboardDisplayDelay(keyboardDisplayDelay: ProductKeyboardDisplayDelay): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapKeyboardDisplayDelayFrom(keyboardDisplayDelay)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateAfterTappingByEnter(afterTappingByEnter: AfterTappingByProductEnter): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapAfterTappingByEnterFrom(afterTappingByEnter)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateAfterAdding(afterAdding: AfterAddingProduct): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapAfterAddingFrom(afterAdding)
            it.plusAssign(newPreferences)
        }
    }

    suspend fun updateAfterEditing(afterEditing: AfterEditingProduct): Unit = withIoContext {
        context.dataStore.edit {
            val newPreferences = mapper.mapAfterEditingFrom(afterEditing)
            it.plusAssign(newPreferences)
        }
    }
}