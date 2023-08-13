package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.*
import java.text.DecimalFormat

class SettingsState {

    private var settings by mutableStateOf(Settings())

    var screenData by mutableStateOf(SettingsScreenData())
        private set

    fun showLoading() {
        screenData = SettingsScreenData(screenState = ScreenState.Loading)
    }

    fun showSetting(settings: Settings) {
        this.settings = settings
        val preferences = settings.appConfig.userPreferences
        val smartphoneScreen = settings.appConfig.deviceConfig.getDeviceSize() == DeviceSize.Medium

        screenData = SettingsScreenData(
            screenState = ScreenState.Showing,
            settings = settings.getSettingsItems(),
            fontSize = preferences.fontSize,
            displayCompletedPurchases = preferences.displayCompleted,
            displayShoppingsProducts = preferences.displayShoppingsProducts,
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

    fun getAppGithubLinkResult(): Result<String> {
        val link = settings.appGithubLink
        return if (link.isEmpty()) {
            Result.failure(Exception())
        } else {
            Result.success(link)
        }
    }

    fun getPrivacyPolicyLinkResult(): Result<String> {
        val link = settings.privacyPolicyLink
        return if (link.isEmpty()) {
            Result.failure(Exception())
        } else {
            Result.success(link)
        }
    }

    fun getTermsAndConditionsLinkResult(): Result<String> {
        val link = settings.termsAndConditionsLink
        return if (link.isEmpty()) {
            Result.failure(Exception())
        } else {
            Result.success(link)
        }
    }

    fun getMoneyFractionDigitsResult(): Result<DecimalFormat> {
        var displayZeros: Boolean? = null
        screenData.settings.values.forEach { items ->
            val item = items.find { it.uid == SettingsUid.DisplayMoneyZeros }
            if (item != null) {
                displayZeros = item.checked
                return@forEach
            }
        }

        return if (displayZeros == null) {
            Result.failure(NullPointerException())
        } else {
            val success = UserPreferencesDefaults.getMoneyDecimalFormat().apply {
                if (displayZeros == true) {
                    minimumFractionDigits = 0
                }
            }
            return Result.success(success)
        }
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