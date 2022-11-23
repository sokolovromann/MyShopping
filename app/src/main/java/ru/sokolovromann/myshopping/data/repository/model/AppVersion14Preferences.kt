package ru.sokolovromann.myshopping.data.repository.model

data class AppVersion14Preferences(
    val firstOpened: Boolean = false,
    val currency: Currency = Currency(),
    val taxRate: TaxRate = TaxRate(),
    val fontSize: FontSize = FontSize.DefaultValue,
    val firstLetterUppercase: Boolean = false,
    val multiColumns: Boolean = false,
    val sort: Sort = Sort(),
    val displayMoney: Boolean = false,
    val displayTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val editCompleted: Boolean = false,
    val addLastProduct: Boolean = false,
)