package ru.sokolovromann.myshopping.ui.compose.event

import ru.sokolovromann.myshopping.ui.DrawerScreen

sealed class SettingsScreenEvent {

    object OnShowBackScreen : SettingsScreenEvent()

    object OnEditCurrency : SettingsScreenEvent()

    object OnEditTaxRate : SettingsScreenEvent()

    data class OnSendEmailToDeveloper(val email: String) : SettingsScreenEvent()

    object OnShowBackup : SettingsScreenEvent()

    data class OnShowAppGithub(val link: String) : SettingsScreenEvent()

    data class OnShowPrivacyPolicy(val link: String) : SettingsScreenEvent()

    data class OnShowTermsAndConditions(val link: String) : SettingsScreenEvent()

    object OnUpdateProductsWidgets : SettingsScreenEvent()

    data class OnDrawerScreenSelected(val drawerScreen: DrawerScreen) : SettingsScreenEvent()

    data class OnSelectDrawerScreen(val display: Boolean) : SettingsScreenEvent()
}