package ru.sokolovromann.myshopping.ui.compose.event

import ru.sokolovromann.myshopping.ui.DrawerScreen

sealed class SettingsScreenEvent {

    object OnShowBackScreen : SettingsScreenEvent()

    object OnEditCurrencyScreen : SettingsScreenEvent()

    object OnEditTaxRateScreen : SettingsScreenEvent()

    @Deprecated("")
    data class OnSendEmailToDeveloper(val email: String) : SettingsScreenEvent()

    object OnShowBackupScreen : SettingsScreenEvent()

    object OnShowFontSizes : SettingsScreenEvent()

    object OnShowDisplayCompleted : SettingsScreenEvent()

    object OnShowMaxAutocompletes : SettingsScreenEvent()

    @Deprecated("")
    data class OnShowAppGithub(val link: String) : SettingsScreenEvent()

    @Deprecated("")
    data class OnShowPrivacyPolicy(val link: String) : SettingsScreenEvent()

    @Deprecated("")
    data class OnShowTermsAndConditions(val link: String) : SettingsScreenEvent()

    object OnUpdateProductsWidgets : SettingsScreenEvent()

    data class OnDrawerScreenSelected(val drawerScreen: DrawerScreen) : SettingsScreenEvent()

    data class OnSelectDrawerScreen(val display: Boolean) : SettingsScreenEvent()
}