package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.model.AppConfig

class MainState {

    var nightTheme: Boolean by mutableStateOf(false)
        private set

    var shoppingUid: String? by mutableStateOf(null)
        private set

    var waiting by mutableStateOf(true)
        private set

    fun populate(appConfig: AppConfig) {
        nightTheme = appConfig.userPreferences.nightTheme
    }

    fun saveShoppingUid(uid: String?) {
        shoppingUid = uid
    }

    fun onWaiting(displaySplashScreen: Boolean) {
        waiting = displaySplashScreen
    }
}