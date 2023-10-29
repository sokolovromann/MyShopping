package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.SettingsWithConfig
import ru.sokolovromann.myshopping.ui.utils.*

class SettingsState {

    private var settingsWithConfig by mutableStateOf(SettingsWithConfig())

    var screenData by mutableStateOf(SettingsScreenData())
        private set

    fun showLoading() {
        screenData = SettingsScreenData(screenState = ScreenState.Loading)
    }

    fun showSetting(settingsWithConfig: SettingsWithConfig) {
        this.settingsWithConfig = settingsWithConfig

        val userPreferences = settingsWithConfig.appConfig.userPreferences
        val smartphoneScreen = settingsWithConfig.appConfig.deviceConfig.getDeviceSize() == DeviceSize.Medium
        screenData = SettingsScreenData(
            screenState = ScreenState.Showing,
            settings = settingsWithConfig.getSettingsItems(),
            fontSize = userPreferences.fontSize,
            displayCompletedPurchases = userPreferences.displayCompleted,
            displayShoppingsProducts = userPreferences.displayShoppingsProducts,
            multiColumns = !smartphoneScreen,
            smartphoneScreen = smartphoneScreen
        )
    }

    fun showFontSize() {
        screenData = screenData.copy(settingsItemUid = SettingsUid.FontSize)
    }

    fun showShoppingsProducts() {
        screenData = screenData.copy(settingsItemUid = SettingsUid.DisplayShoppingsProducts)
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

    fun hideDisplayShoppingsProducts() {
        screenData = screenData.copy(settingsItemUid = null)
    }

    fun getAppGithubLink(): String {
        return settingsWithConfig.settings.appGithubLink
    }

    fun getPrivacyPolicyLink(): String {
        return settingsWithConfig.settings.privacyPolicyLink
    }

    fun getTermsAndConditionsLink(): String {
        return settingsWithConfig.settings.termsAndConditionsLink
    }

    fun getDeveloperEmail(): String {
        return settingsWithConfig.settings.developerEmail
    }
}

data class SettingsScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val settings: Map<UiText, List<SettingsItem>> = mapOf(),
    val settingsItemUid: SettingsUid? = null,
    val fontSize: FontSize = FontSize.DefaultValue,
    val displayCompletedPurchases: DisplayCompleted = DisplayCompleted.DefaultValue,
    val displayShoppingsProducts: DisplayProducts = DisplayProducts.DefaultValue,
    val multiColumns: Boolean = false,
    val smartphoneScreen: Boolean = true,
)