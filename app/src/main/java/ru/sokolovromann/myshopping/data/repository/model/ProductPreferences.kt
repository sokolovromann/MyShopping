package ru.sokolovromann.myshopping.data.repository.model

data class ProductPreferences(
    val currency: Currency = Currency(),
    val taxRate: TaxRate = TaxRate(),
    val fontSize: FontSize = FontSize.DefaultValue,
    val firstLetterUppercase: Boolean = true,
    val multiColumns: Boolean = false,
    val sort: Sort = Sort(),
    val displayMoney: Boolean = true,
    val displayCompleted: DisplayCompleted = DisplayCompleted.DefaultValue,
    val displayTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val displayAutocomplete: DisplayAutocomplete = DisplayAutocomplete.DefaultValue,
    val lockQuantity: Boolean = false,
    val editCompleted: Boolean = false,
    val addLastProduct: Boolean = false,
    val screenSize: ScreenSize = ScreenSize.DefaultValue
)