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
    Backup,
    DisplayMoney,
    Currency,
    DisplayCurrencyToLeft,
    DisplayMoneyZeros,
    TaxRate,
    DisplayDefaultAutocomplete,
    DisplayCompletedPurchases,
    EditProductAfterCompleted,
    CompletedWithCheckbox,
    DisplayShoppingsProducts,
    DisplayOtherFields,
    EnterToSaveProducts,
    ColoredCheckbox,
    SaveProductToAutocompletes,
    Email,
    Github,
    PrivacyPolicy,
    TermsAndConditions
}