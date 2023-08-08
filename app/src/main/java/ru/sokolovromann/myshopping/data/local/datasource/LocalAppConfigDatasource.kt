package ru.sokolovromann.myshopping.data.local.datasource

import android.content.Context
import android.content.res.Resources
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import javax.inject.Inject

class LocalAppConfigDatasource @Inject constructor(
    private val context: Context
) {

    private val preferencesFileName = "local_datastore"
    private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(
        name = preferencesFileName
    )

    fun getPreferences(): DataStore<Preferences> {
        return context.preferencesDataStore
    }

    fun getResources(): Resources {
        return context.resources
    }
}