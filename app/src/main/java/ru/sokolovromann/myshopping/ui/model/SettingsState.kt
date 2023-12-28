package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.Settings
import ru.sokolovromann.myshopping.data.model.SettingsWithConfig
import ru.sokolovromann.myshopping.ui.model.mapper.UiAppConfigMapper

class SettingsState {

    private var settingsWithConfig by mutableStateOf(SettingsWithConfig())

    var settings: Map<UiString, List<SettingItem>> by mutableStateOf(mapOf())
        private set

    var selectedUid: SettingUid? by mutableStateOf(null)
        private set

    var displayCompletedValue: SelectedValue<DisplayCompleted> by mutableStateOf(SelectedValue(DisplayCompleted.DefaultValue))
        private set

    var displayProductsValue: SelectedValue<DisplayProducts> by mutableStateOf(SelectedValue(DisplayProducts.DefaultValue))
        private set

    var multiColumns: Boolean by mutableStateOf(false)
        private set

    var smartphoneScreen: Boolean by mutableStateOf(false)
        private set

    var fontSizeValue: SelectedValue<FontSize> by mutableStateOf(SelectedValue(FontSize.DefaultValue))
        private set

    var fontSize: UiFontSize by mutableStateOf(UiFontSize.Default)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(settingsWithConfig: SettingsWithConfig) {
        this.settingsWithConfig = settingsWithConfig

        val userPreferences = settingsWithConfig.appConfig.userPreferences
        settings = UiAppConfigMapper.toSettingItems(settingsWithConfig)
        selectedUid = null
        displayCompletedValue = toDisplayCompletedValue(userPreferences.displayCompleted)
        displayProductsValue = toDisplayProductsValue(userPreferences.displayShoppingsProducts)
        smartphoneScreen = settingsWithConfig.appConfig.deviceConfig.getDeviceSize().isSmartphoneScreen()
        multiColumns = !smartphoneScreen
        fontSizeValue = toFontSizeValue(userPreferences.fontSize)
        fontSize = UiAppConfigMapper.toUiFontSize(userPreferences.fontSize)
        waiting = false
    }

    fun onSelectUid(expanded: Boolean, settingUid: SettingUid) {
        selectedUid = if (expanded) settingUid else null
    }

    fun onWaiting() {
        waiting = true
    }

    fun getSettings(): Settings {
        return settingsWithConfig.settings
    }

    private fun toDisplayCompletedValue(
        displayCompleted: DisplayCompleted
    ): SelectedValue<DisplayCompleted> {
        return SelectedValue(
            selected = displayCompleted,
            text = when (displayCompleted) {
                DisplayCompleted.FIRST -> UiString.FromResources(R.string.settings_action_displayCompletedPurchasesFirst)
                DisplayCompleted.LAST -> UiString.FromResources(R.string.settings_action_displayCompletedPurchasesLast)
                DisplayCompleted.HIDE -> UiString.FromResources(R.string.settings_action_hideCompletedPurchases)
                DisplayCompleted.NO_SPLIT -> UiString.FromResources(R.string.settings_action_noSplitCompletedPurchases)
            }
        )
    }

    private fun toDisplayProductsValue(
        displayProducts: DisplayProducts
    ): SelectedValue<DisplayProducts> {
        return SelectedValue(
            selected = displayProducts,
            text = when (displayProducts) {
                DisplayProducts.VERTICAL -> UiString.FromResources(R.string.settings_action_displayShoppingsProductsColumns)
                DisplayProducts.HORIZONTAL -> UiString.FromResources(R.string.settings_action_displayShoppingsProductsRow)
                DisplayProducts.HIDE -> UiString.FromResources(R.string.settings_action_hideShoppingsProducts)
                DisplayProducts.HIDE_IF_HAS_TITLE -> UiString.FromResources(R.string.settings_action_hideShoppingsProductsIfHasTitle)
            }
        )
    }

    private fun toFontSizeValue(fontSize: FontSize): SelectedValue<FontSize> {
        return SelectedValue(
            selected = fontSize,
            text = when (fontSize) {
                FontSize.SMALL -> UiString.FromResources(R.string.settings_action_selectSmallFontSize)
                FontSize.MEDIUM -> UiString.FromResources(R.string.settings_action_selectMediumFontSize)
                FontSize.LARGE -> UiString.FromResources(R.string.settings_action_selectLargeFontSize)
                FontSize.VERY_LARGE -> UiString.FromResources(R.string.settings_action_selectVeryLargeFontSize)
                FontSize.HUGE -> UiString.FromResources(R.string.settings_action_selectHugeFontSize)
                FontSize.VERY_HUGE -> UiString.FromResources(R.string.settings_action_selectVeryHugeFontSize)
            }
        )
    }
}