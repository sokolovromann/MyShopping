package ru.sokolovromann.myshopping.data39.settings.user

import androidx.datastore.preferences.core.stringPreferencesKey

object UserConfigScheme {
    const val DATA_STORE_NAME: String = "local_user_config"

    val API = stringPreferencesKey("api")
}