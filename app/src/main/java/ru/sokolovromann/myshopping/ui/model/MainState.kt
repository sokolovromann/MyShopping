package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.model.NightTheme
import ru.sokolovromann.myshopping.data.model.UserPreferences
import ru.sokolovromann.myshopping.ui.model.mapper.UiAppConfigMapper
import ru.sokolovromann.myshopping.ui.theme.FontSizeOffset

class MainState {

    var nightTheme: NightTheme by mutableStateOf(NightTheme.DefaultValue)
        private set

    var fontSizeOffset: FontSizeOffset by mutableStateOf(FontSizeOffset())
        private set

    var shoppingUid: String? by mutableStateOf(null)
        private set

    var waiting by mutableStateOf(true)
        private set

    fun populate(userPreferences: UserPreferences) {
        nightTheme = userPreferences.nightTheme
        fontSizeOffset = UiAppConfigMapper.toFontSizeOffset(userPreferences.appFontSize)
    }

    fun saveShoppingUid(uid: String?) {
        shoppingUid = uid
    }

    fun onWaiting(displaySplashScreen: Boolean) {
        waiting = displaySplashScreen
    }
}