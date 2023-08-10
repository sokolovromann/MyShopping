package ru.sokolovromann.myshopping.data.local.datasource

import android.content.Context
import android.content.SharedPreferences
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

    fun getCodeVersion14Preferences(): SharedPreferences = context
        .getSharedPreferences("MyPref", Context.MODE_PRIVATE)

    fun getCodeVersion14firstOpenedPreferences(): SharedPreferences = context
        .getSharedPreferences("First", Context.MODE_PRIVATE)

    fun getResources(): Resources {
        return context.resources
    }
}