package ru.sokolovromann.myshopping.data.repository.model

data class AppPreferences(
    val appOpenedAction: AppOpenedAction = AppOpenedAction.DefaultValue,
    val nightTheme: Boolean = false,
    val fontSize: FontSize = FontSize.DefaultValue,
    val smartphoneScreen: Boolean = true,
    val currency: Currency = Currency(),
    val taxRate: TaxRate = TaxRate(),
    val shoppingsMultiColumns: Boolean = false,
    val productsMultiColumns: Boolean = false,
    val displayCompletedPurchases: DisplayCompleted = DisplayCompleted.DefaultValue,
    val displayPurchasesTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val editProductAfterCompleted: Boolean = false,
    val saveProductToAutocompletes: Boolean = true,
    val lockProductElement: LockProductElement = LockProductElement.DefaultValue,
    val displayMoney: Boolean = true,
    val displayDefaultAutocompletes: Boolean = true
)