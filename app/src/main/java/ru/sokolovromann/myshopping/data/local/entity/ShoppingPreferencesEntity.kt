package ru.sokolovromann.myshopping.data.local.entity

data class ShoppingPreferencesEntity(
    val currency: String = "",
    val currencyDisplayToLeft: Boolean = false,
    val taxRate: Float = 0f,
    val taxRateAsPercent: Boolean = false,
    val fontSize: String = "",
    val firstLetterUppercase: Boolean = false,
    val multiColumns: Boolean = false,
    val displayMoney: Boolean = false,
    val displayCompleted: String = "",
    val displayTotal: String = "",
    val screenSize: String = "",
    val maxProducts: Int = 0
)