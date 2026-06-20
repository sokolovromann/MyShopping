package ru.sokolovromann.myshopping.core.data.datasource

import androidx.datastore.preferences.core.stringPreferencesKey

object BackupPreferencesScheme {

    const val FILE_NAME = "backup_preferences"
    val DIRECTORY_KEY = stringPreferencesKey("directory")
}