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
            displayAutocomplete = settings.settingsValues.productsDisplayAutocomplete,
            multiColumns = preferences.screenSize == ScreenSize.TABLET
        )
    }

    fun showFontSize() {
        screenData = screenData.copy(settingsItemUid = SettingsUid.FontSize)
    }

    fun showDisplayAutocomplete() {
        screenData = screenData.copy(settingsItemUid = SettingsUid.DisplayAutocomplete)
    }

    fun hideFontSize() {
        screenData = screenData.copy(settingsItemUid = null)
    }

    fun hideDisplayAutocomplete() {
        screenData = screenData.copy(settingsItemUid = null)
    }
}

data class SettingsScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val settings: Map<UiText, List<SettingsItem>> = mapOf(),
    val settingsItemUid: SettingsUid? = null,
    val fontSize: FontSize = FontSize.DefaultValue,
    val displayAutocomplete: DisplayAutocomplete = DisplayAutocomplete.DefaultValue,
    val multiColumns: Boolean = false
)