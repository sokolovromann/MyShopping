package ru.sokolovromann.myshopping.data.local.datasource

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Environment
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import javax.inject.Inject

class AppContent @Inject constructor(
    private val context: Context
) {

    companion object {
        private const val PREFERENCES_NAME = "local_datastore"
        private const val USER_SHARED_PREFERENCES_NAME = "MyPref"
        private const val OPENED_SHARED_PREFERENCES_NAME = "First"

        fun getAppFolderRelativePath(): String {
            return "${Environment.DIRECTORY_DOCUMENTS}/MyShoppingList"
        }

        fun getAppFolderAbsolutePath(): String {
            return "${Environment.getExternalStorageDirectory()}/${getAppFolderRelativePath()}"
        }
    }

    private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(
        name = PREFERENCES_NAME
    )

    fun getPreferences(): DataStore<Preferences> {
        return context.preferencesDataStore
    }

    fun getUserSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(USER_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun getOpenedSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(OPENED_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun getResources(): Resources {
        return context.resources
    }

    fun getContentResolver(): ContentResolver {
        return context.contentResolver
    }
}