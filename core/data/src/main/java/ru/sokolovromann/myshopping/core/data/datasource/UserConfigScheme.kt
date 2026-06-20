package ru.sokolovromann.myshopping.core.data.datasource

import androidx.datastore.preferences.core.stringPreferencesKey

object UserConfigScheme {

    const val FILE_NAME = "user_config"
    val API_KEY = stringPreferencesKey("api")
}