package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.ui.DrawerScreen
import ru.sokolovromann.myshopping.ui.model.SettingUid

sealed class SettingsEvent {

    object OnClickBack : SettingsEvent()

    data class OnSettingItemSelected(val uid: SettingUid) : SettingsEvent()

    data class OnSelectSettingItem(val expanded: Boolean, val uid: SettingUid) : SettingsEvent()

    data class OnDrawerScreenSelected(val drawerScreen: DrawerScreen) : SettingsEvent()

    data class OnSelectDrawerScreen(val display: Boolean) : SettingsEvent()

    data class OnFontSizeSelected(val fontSize: FontSize) : SettingsEvent()

    data class OnDisplayCompletedSelected(val displayCompleted: DisplayCompleted) : SettingsEvent()

    data class OnDisplayProductsSelected(val displayProducts: DisplayProducts) : SettingsEvent()
}