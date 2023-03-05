package ru.sokolovromann.myshopping.data.local.entity

data class AppPreferencesEntity(
    val appOpenedAction: String = "",
    val nightTheme: Boolean = false,
    val fontSize: String = "",
    val smartphoneScreen: Boolean = true,
    val currency: String = "",
    val displayCurrencyToLeft: Boolean = false,
    val taxRate: Float = 0f,
    val taxRateAsPercent: Boolean = false,
    val shoppingsMultiColumns: Boolean = false,
    val productsMultiColumns: Boolean = false,
    val displayCompletedPurchases: String = "",
    val displayPurchasesTotal: String = "",
    val editProductAfterCompleted: Boolean = false,
    val saveProductToAutocompletes: Boolean = true,
    val lockProductElement: String = "",
    val displayMoney: Boolean = true,
    val displayDefaultAutocompletes: Boolean = true
)