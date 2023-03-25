package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.*

class SettingsState {

    private var settings by mutableStateOf(Settings())

    var screenData by mutableStateOf(SettingsScreenData())
        private set

    fun showLoading() {
        screenData = SettingsScreenData(screenState = ScreenState.Loading)
    }

    fun showSetting(settings: Settings) {
        this.settings = settings
        val preferences = settings.preferences

        screenData = SettingsScreenData(
            screenState = ScreenState.Showing,
            settings = settings.getSettingsItems(),
            fontSize = preferences.fontSize,
            displayCompletedPurchases = preferences.displayCompletedPurchases,
            multiColumns = !preferences.smartphoneScreen,
            smartphoneScreen = preferences.smartphoneScreen
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
}

data class SettingsScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val settings: Map<UiText, List<SettingsItem>> = mapOf(),
    val settingsItemUid: SettingsUid? = null,
    val fontSize: FontSize = FontSize.DefaultValue,
    val displayCompletedPurchases: DisplayCompleted = DisplayCompleted.DefaultValue,
    val multiColumns: Boolean = false,
    val smartphoneScreen: Boolean = true,
)