package ru.sokolovromann.myshopping.data.repository.model

data class ShoppingListPreferences(
    val currency: Currency = Currency(),
    val taxRate: TaxRate = TaxRate(),
    val fontSize: FontSize = FontSize.DefaultValue,
    val firstLetterUppercase: Boolean = true,
    val multiColumns: Boolean = false,
    val sort: Sort = Sort(),
    val displayMoney: Boolean = true,
    val displayCompleted: DisplayCompleted = DisplayCompleted.DefaultValue,
    val displayTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val screenSize: ScreenSize = ScreenSize.DefaultValue,
    val maxProducts: Int = 0
)