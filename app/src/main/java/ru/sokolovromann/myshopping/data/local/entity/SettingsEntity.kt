package ru.sokolovromann.myshopping.data.local.entity

data class SettingsEntity(
    val nightTheme: Boolean = false,
    val currency: String = "",
    val currencyDisplayToLeft: Boolean = false,
    val taxRate: Float = 0f,
    val taxRateAsPercent: Boolean = false,
    val fontSize: String = "",
    val firstLetterUppercase: Boolean = false,
    val displayMoney: Boolean = false,
    val displayCompleted: String,
    val shoppingsMultiColumns: Boolean = false,
    val productsMultiColumns: Boolean = false,
    val productsDisplayAutocomplete: String = "",
    val productsEditCompleted: Boolean = false,
    val productsAddLastProduct: Boolean = false,
    val productsDisplayDefaultAutocomplete: Boolean
)