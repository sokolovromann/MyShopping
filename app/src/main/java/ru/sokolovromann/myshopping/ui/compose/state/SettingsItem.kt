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
    FirstLetterUppercase,
    DisplayMoney,
    Currency,
    DisplayCurrencyToLeft,
    TaxRate,
    ShoppingsMultiColumns,
    ProductsMultiColumns,
    DisplayAutocomplete,
    EditCompleted,
    AddProduct,
    Email,
    Github
}