package ru.sokolovromann.myshopping.core.data.datasource

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object LocalDataStore {

    const val GENERAL_FILE_NAME = "api42_general_preferences"
    const val CARTS_FILE_NAME = "api42_carts_preferences"
    const val PRODUCTS_FILE_NAME = "api42_products_preferences"
    const val PRODUCTS_WIDGET_FILE_NAME = "api42_products_widget_preferences"
    const val ADD_EDIT_PRODUCT_FILE_NAME = "api42_add_edit_product_preferences"
    const val SUGGESTIONS_FILE_NAME = "api42_suggestions_preferences"
    const val BACKUP_FILE_NAME = "api42_backup_preferences"
    const val USER_FILE_NAME = "api42_user_config"

    fun <M : DataMigration<Preferences>> build(
        context: Context,
        migration: M,
        fileName: String
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        migrations = listOf(migration),
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        produceFile = { context.preferencesDataStoreFile(fileName) }
    )
}