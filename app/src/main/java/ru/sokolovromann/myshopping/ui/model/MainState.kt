package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.model.AfterAddShopping
import ru.sokolovromann.myshopping.data.model.NightTheme
import ru.sokolovromann.myshopping.data.model.UserPreferences
import ru.sokolovromann.myshopping.ui.model.mapper.UiAppConfigMapper
import ru.sokolovromann.myshopping.ui.theme.FontSizeOffset

class MainState {

    var nightTheme: NightTheme by mutableStateOf(NightTheme.DefaultValue)
        private set

    var fontSizeOffset: FontSizeOffset by mutableStateOf(FontSizeOffset())
        private set

    var afterAddShopping: AfterAddShopping? by mutableStateOf(AfterAddShopping.DefaultValue)
        private set

    var shoppingUid: String? by mutableStateOf(null)
        private set

    var waiting by mutableStateOf(true)
        private set

    fun populate(userPreferences: UserPreferences) {
        nightTheme = userPreferences.nightTheme
        fontSizeOffset = UiAppConfigMapper.toFontSizeOffset(userPreferences.appFontSize)
        afterAddShopping = userPreferences.afterAddShopping
    }

    fun saveShoppingUid(uid: String?, after: AfterAddShopping? = null) {
        shoppingUid = uid
        afterAddShopping = after
    }

    fun onWaiting(displaySplashScreen: Boolean) {
        waiting = displaySplashScreen
    }
}