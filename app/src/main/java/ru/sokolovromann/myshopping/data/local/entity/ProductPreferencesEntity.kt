package ru.sokolovromann.myshopping.data.local.entity

data class ProductPreferencesEntity(
    val currency: String = "",
    val currencyDisplayToLeft: Boolean = false,
    val taxRate: Float = 0f,
    val taxRateAsPercent: Boolean = false,
    val fontSize: String = "",
    val firstLetterUppercase: Boolean = false,
    val multiColumns: Boolean = false,
    val sortBy: String = "",
    val sortAscending: Boolean = false,
    val displayMoney: Boolean = false,
    val displayCompleted: String = "",
    val displayTotal: String = "",
    val displayAutocomplete: String = "",
    val lockQuantity: Boolean = false,
    val editCompleted: Boolean = false,
    val addLastProduct: Boolean = false,
    val screenSize: String = ""
)