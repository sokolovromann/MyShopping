package ru.sokolovromann.myshopping.data.local.entity

data class MainPreferencesEntity(
    val appOpenedAction: String = "",
    val nightTheme: Boolean = false,
    val currency: String = "",
    val currencyDisplayToLeft: Boolean = false,
    val taxRate: Float = 0f,
    val taxRateAsPercent: Boolean = false,
    val fontSize: String = "",
    val firstLetterUppercase: Boolean = true,
    val shoppingsMultiColumns: Boolean = false,
    val shoppingsDisplayTotal: String = "",
    val shoppingsMaxProducts: Int = 10,
    val productsMultiColumns: Boolean = false,
    val productsDisplayTotal: String = "",
    val productsDisplayAutocomplete: String = "",
    val productsProductLock: String = "",
    val productsEditCompleted: Boolean = false,
    val productsAddLastProduct: Boolean = true,
    val productsDisplayDefaultAutocomplete: Boolean = true,
    val displayMoney: Boolean = true,
    val displayCompleted: String = "",
    val screenSize: String = ""
)