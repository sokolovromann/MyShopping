package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.NightTheme
import ru.sokolovromann.myshopping.data.model.Settings
import ru.sokolovromann.myshopping.data.model.SettingsWithConfig
import ru.sokolovromann.myshopping.ui.model.mapper.UiAppConfigMapper

class SettingsState {

    private var settingsWithConfig by mutableStateOf(SettingsWithConfig())

    var settings: Map<UiString, List<SettingItem>> by mutableStateOf(mapOf())
        private set

    var selectedUid: SettingUid? by mutableStateOf(null)
        private set

    var nightTheme: SelectedValue<NightTheme> by mutableStateOf(SelectedValue(NightTheme.DefaultValue))
        private set

    var displayProductsValue: SelectedValue<DisplayProducts> by mutableStateOf(SelectedValue(DisplayProducts.DefaultValue))
        private set

    var multiColumns: Boolean by mutableStateOf(false)
        private set

    var deviceSize: DeviceSize by mutableStateOf(DeviceSize.DefaultValue)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(settingsWithConfig: SettingsWithConfig) {
        this.settingsWithConfig = settingsWithConfig

        val userPreferences = settingsWithConfig.appConfig.userPreferences
        settings = UiAppConfigMapper.toSettingItems(settingsWithConfig)
        selectedUid = null
        nightTheme = toNightThemeValue(userPreferences.nightTheme)
        displayProductsValue = toDisplayProductsValue(userPreferences.displayShoppingsProducts)
        deviceSize = settingsWithConfig.appConfig.deviceConfig.getDeviceSize()
        multiColumns = !deviceSize.isSmartphoneScreen()
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

    private fun toNightThemeValue(
        nightTheme: NightTheme
    ): SelectedValue<NightTheme> {
        return SelectedValue(
            selected = nightTheme,
            text = when (nightTheme) {
                NightTheme.DISABLED -> UiString.FromResources(R.string.settings_action_selectDisabledNightTheme)
                NightTheme.APP -> UiString.FromResources(R.string.settings_action_selectAppNightTheme)
                NightTheme.WIDGET -> UiString.FromResources(R.string.settings_action_selectWidgetNightTheme)
                NightTheme.APP_AND_WIDGET -> UiString.FromResources(R.string.settings_action_selectAppAndWidgetNightTheme)
            }
        )
    }

    private fun toDisplayProductsValue(
        displayProducts: DisplayProducts
    ): SelectedValue<DisplayProducts> {
        return SelectedValue(
            selected = displayProducts,
            text = when (displayProducts) {
                DisplayProducts.VERTICAL -> UiString.FromResources(R.string.shoppingLists_action_displayProductsVertically)
                DisplayProducts.HORIZONTAL -> UiString.FromResources(R.string.shoppingLists_action_displayProductsHorizontally)
                DisplayProducts.HIDE -> UiString.FromResources(R.string.shoppingLists_action_hideProducts)
                DisplayProducts.HIDE_IF_HAS_TITLE -> UiString.FromResources(R.string.shoppingLists_action_hideProductsIfHasTitle)
            }
        )
    }
}