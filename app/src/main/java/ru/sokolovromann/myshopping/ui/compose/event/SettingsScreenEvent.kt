package ru.sokolovromann.myshopping.ui.compose.event

sealed class SettingsScreenEvent {

    object EditCurrency : SettingsScreenEvent()

    object EditTaxRate : SettingsScreenEvent()

    data class SendEmailToDeveloper(val email: String) : SettingsScreenEvent()

    object ShowBackScreen : SettingsScreenEvent()

    object ShowNavigationDrawer : SettingsScreenEvent()

    data class ShowAppGithub(val link: String) : SettingsScreenEvent()

    object HideNavigationDrawer : SettingsScreenEvent()
}