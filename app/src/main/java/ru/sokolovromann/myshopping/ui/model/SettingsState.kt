package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.AfterAddShopping
import ru.sokolovromann.myshopping.data.model.AfterProductCompleted
import ru.sokolovromann.myshopping.data.model.AfterSaveProduct
import ru.sokolovromann.myshopping.data.model.AfterShoppingCompleted
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.data.model.NightTheme
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

    var afterSaveProduct: SelectedValue<AfterSaveProduct> by mutableStateOf(SelectedValue(AfterSaveProduct.DefaultValue))
        private set

    var afterProductCompleted: SelectedValue<AfterProductCompleted> by mutableStateOf(SelectedValue(AfterProductCompleted.DefaultValue))
        private set

    var afterAddShopping: SelectedValue<AfterAddShopping> by mutableStateOf(SelectedValue(AfterAddShopping.DefaultValue))
        private set

    var afterShoppingCompleted: SelectedValue<AfterShoppingCompleted> by mutableStateOf(SelectedValue(AfterShoppingCompleted.DefaultValue))
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
        afterSaveProduct = toAfterSaveProductValue(userPreferences.afterSaveProduct)
        afterProductCompleted = toAfterProductCompletedValue(userPreferences.afterProductCompleted)
        afterAddShopping = toAfterAddShoppingValue(userPreferences.afterAddShopping)
        afterShoppingCompleted = toAfterShoppingCompletedValue(userPreferences.afterShoppingCompleted)
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

    private fun toAfterSaveProductValue(
        afterSaveProduct: AfterSaveProduct
    ): SelectedValue<AfterSaveProduct> {
        return SelectedValue(
            selected = afterSaveProduct,
            text = when (afterSaveProduct) {
                AfterSaveProduct.NOTHING -> UiString.FromResources(R.string.settings_action_nothingAfterSaveProduct)
                AfterSaveProduct.CLOSE_SCREEN -> UiString.FromResources(R.string.settings_action_closeAfterSaveProduct)
                AfterSaveProduct.OPEN_NEW_SCREEN -> UiString.FromResources(R.string.settings_action_openAfterSaveProduct)
            }
        )
    }

    private fun toAfterProductCompletedValue(
        afterProductCompleted: AfterProductCompleted
    ): SelectedValue<AfterProductCompleted> {
        return SelectedValue(
            selected = afterProductCompleted,
            text = when (afterProductCompleted) {
                AfterProductCompleted.NOTHING -> UiString.FromResources(R.string.settings_action_nothingAfterProductCompleted)
                AfterProductCompleted.EDIT -> UiString.FromResources(R.string.settings_action_editAfterProductCompleted)
                AfterProductCompleted.DELETE -> UiString.FromResources(R.string.settings_action_deleteAfterProductCompleted)
            }
        )
    }

    private fun toAfterAddShoppingValue(
        afterAddShopping: AfterAddShopping
    ): SelectedValue<AfterAddShopping> {
        return SelectedValue(
            selected = afterAddShopping,
            text = when (afterAddShopping) {
                AfterAddShopping.OPEN_PRODUCTS_SCREEN -> UiString.FromResources(R.string.settings_action_openProductsAfterAddShopping)
                AfterAddShopping.OPEN_EDIT_SHOPPING_NAME_SCREEN -> UiString.FromResources(R.string.settings_action_openEditNameAfterAddShopping)
                AfterAddShopping.OPEN_ADD_PRODUCT_SCREEN -> UiString.FromResources(R.string.settings_action_openAddProductAfterAddShopping)
            }
        )
    }

    private fun toAfterShoppingCompletedValue(
        afterShoppingCompleted: AfterShoppingCompleted
    ): SelectedValue<AfterShoppingCompleted> {
        return SelectedValue(
            selected = afterShoppingCompleted,
            text = when (afterShoppingCompleted) {
                AfterShoppingCompleted.NOTHING -> UiString.FromResources(R.string.settings_action_nothingAfterShoppingCompleted)
                AfterShoppingCompleted.ARCHIVE -> UiString.FromResources(R.string.settings_action_archiveAfterShoppingCompleted)
                AfterShoppingCompleted.DELETE -> UiString.FromResources(R.string.settings_action_deleteAfterShoppingCompleted)
                AfterShoppingCompleted.DELETE_PRODUCTS -> UiString.FromResources(R.string.settings_action_deleteProductsAfterShoppingCompleted)
                AfterShoppingCompleted.DELETE_LIST_AND_PRODUCTS -> UiString.FromResources(R.string.settings_action_deleteListAndProductsAfterShoppingCompleted)
            }
        )
    }
}