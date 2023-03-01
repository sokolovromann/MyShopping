package ru.sokolovromann.myshopping.data.repository.model

data class MainPreferences(
    val appOpenedAction: AppOpenedAction = AppOpenedAction.DefaultValue,
    val nightTheme: Boolean = false,
    val currency: Currency = Currency(),
    val taxRate: TaxRate = TaxRate(),
    val fontSize: FontSize = FontSize.DefaultValue,
    val firstLetterUppercase: Boolean = true,
    val shoppingsMultiColumns: Boolean = false,
    val shoppingsDisplayTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val shoppingsMaxProducts: Int = 10,
    val productsMultiColumns: Boolean = false,
    val productsDisplayTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val productsDisplayAutocomplete: DisplayAutocomplete = DisplayAutocomplete.DefaultValue,
    val productsProductLock: ProductLock = ProductLock.DefaultValue,
    val productsEditCompleted: Boolean = false,
    val productsAddLastProduct: Boolean = true,
    val productsDisplayDefaultAutocomplete: Boolean = true,
    val displayMoney: Boolean = true,
    val displayCompleted: DisplayCompleted = DisplayCompleted.DefaultValue,
    val screenSize: ScreenSize = ScreenSize.DefaultValue
)