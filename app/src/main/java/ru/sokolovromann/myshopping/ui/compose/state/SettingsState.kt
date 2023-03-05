package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.*

class SettingsState {

    var screenData by mutableStateOf(SettingsScreenData())
        private set

    fun showLoading() {
        screenData = SettingsScreenData(screenState = ScreenState.Loading)
    }

    fun showSetting(settings: Settings) {
        val preferences = settings.preferences

        screenData = SettingsScreenData(
            screenState = ScreenState.Showing,
            settings = settings.getSettingsItems(),
            fontSize = preferences.fontSize,
            displayCompletedPurchases = preferences.displayCompletedPurchases,
            multiColumns = !preferences.smartphoneScreen
        )
    }

    fun showFontSize() {
        screenData = screenData.copy(settingsItemUid = SettingsUid.FontSize)
    }

    fun showDisplayCompletedPurchases() {
        screenData = screenData.copy(settingsItemUid = SettingsUid.DisplayCompletedPurchases)
    }

    fun hideFontSize() {
        screenData = screenData.copy(settingsItemUid = null)
    }

    fun hideDisplayCompletedPurchases() {
        screenData = screenData.copy(settingsItemUid = null)
    }
}

data class SettingsScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val settings: Map<UiText, List<SettingsItem>> = mapOf(),
    val settingsItemUid: SettingsUid? = null,
    val fontSize: FontSize = FontSize.DefaultValue,
    val displayCompletedPurchases: DisplayCompleted = DisplayCompleted.DefaultValue,
    val multiColumns: Boolean = false
)