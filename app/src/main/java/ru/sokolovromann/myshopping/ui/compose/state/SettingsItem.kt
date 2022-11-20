package ru.sokolovromann.myshopping.ui.compose.state

data class SettingsItem(
    val uid: SettingsUid = SettingsUid.NoUId,
    val title: TextData = TextData(),
    val body: TextData = TextData(),
    val checked: SwitchData? = null
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