package ru.sokolovromann.myshopping.core.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile

object LocalDataStore {

    const val GENERAL_FILE_NAME = "api42_general_preferences"
    const val CARTS_FILE_NAME = "api42_carts_preferences"
    const val PRODUCTS_FILE_NAME = "api42_products_preferences"
    const val PRODUCTS_WIDGET_FILE_NAME = "api42_products_widget_preferences"
    const val ADD_EDIT_PRODUCT_FILE_NAME = "api42_add_edit_product_preferences"
    const val SUGGESTIONS_FILE_NAME = "api42_suggestions_preferences"
    const val BACKUP_FILE_NAME = "api42_backup_preferences"
    const val USER_FILE_NAME = "api42_user_config"

    fun build(context: Context, fileName: String): DataStore<Preferences> =
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile(fileName)
        }
}