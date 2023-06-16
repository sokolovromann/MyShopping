package ru.sokolovromann.myshopping.ui.compose.event

sealed class SettingsScreenEvent {

    object EditCurrency : SettingsScreenEvent()

    object EditTaxRate : SettingsScreenEvent()

    data class SendEmailToDeveloper(val email: String) : SettingsScreenEvent()

    object ShowBackScreen : SettingsScreenEvent()

    object ShowPurchases : SettingsScreenEvent()

    object ShowArchive : SettingsScreenEvent()

    object ShowTrash : SettingsScreenEvent()

    object ShowAutocompletes : SettingsScreenEvent()

    object ShowNavigationDrawer : SettingsScreenEvent()

    data class ShowAppGithub(val link: String) : SettingsScreenEvent()

    data class ShowPrivacyPolicy(val link: String) : SettingsScreenEvent()

    data class ShowTermsAndConditions(val link: String) : SettingsScreenEvent()

    object HideNavigationDrawer : SettingsScreenEvent()

    object UpdateProductsWidgets : SettingsScreenEvent()
}