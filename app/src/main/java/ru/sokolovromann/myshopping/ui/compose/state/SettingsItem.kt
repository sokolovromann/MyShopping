package ru.sokolovromann.myshopping.ui.compose.state

data class SettingsItem(
    val uid: SettingsUid = SettingsUid.NoUId,
    val titleText: UiText = UiText.Nothing,
    val bodyText: UiText = UiText.Nothing,
    val checked: Boolean? = null
)

enum class SettingsUid {
    NoUId,
    NightTheme,
    FontSize,
    DisplayMoney,
    Currency,
    DisplayCurrencyToLeft,
    TaxRate,
    DisplayDefaultAutocomplete,
    DisplayCompletedPurchases,
    EditProductAfterCompleted,
    CompletedWithCheckbox,
    DisplayShoppingsProducts,
    EnterToSaveProducts,
    HighlightCheckbox,
    SaveProductToAutocompletes,
    MigrateFromAppVersion14,
    Email,
    Github,
    PrivacyPolicy,
    TermsAndConditions
}