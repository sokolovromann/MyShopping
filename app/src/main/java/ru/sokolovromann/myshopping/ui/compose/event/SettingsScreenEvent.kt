package ru.sokolovromann.myshopping.ui.compose.event

import ru.sokolovromann.myshopping.ui.DrawerScreen

sealed class SettingsScreenEvent {

    object OnShowBackScreen : SettingsScreenEvent()

    object OnEditCurrencyScreen : SettingsScreenEvent()

    object OnEditTaxRateScreen : SettingsScreenEvent()

    object OnShowBackupScreen : SettingsScreenEvent()

    object OnShowFontSizes : SettingsScreenEvent()

    object OnShowDisplayCompleted : SettingsScreenEvent()

    object OnShowMaxAutocompletes : SettingsScreenEvent()

    object OnShowSwipeProduct : SettingsScreenEvent()

    object OnUpdateProductsWidgets : SettingsScreenEvent()

    data class OnDrawerScreenSelected(val drawerScreen: DrawerScreen) : SettingsScreenEvent()

    data class OnSelectDrawerScreen(val display: Boolean) : SettingsScreenEvent()
}