package ru.sokolovromann.myshopping.data.repository.model

data class AppVersion14Preferences(
    val firstOpened: Boolean = false,
    val currency: Currency = Currency(),
    val taxRate: TaxRate = TaxRate(),
    val fontSize: FontSize = FontSize.DefaultValue,
    val multiColumns: Boolean = false,
    val displayMoney: Boolean = false,
    val displayTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val editProductAfterCompleted: Boolean = false,
    val saveProductToAutocompletes: Boolean = false
)