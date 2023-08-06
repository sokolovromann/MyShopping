package ru.sokolovromann.myshopping.data.repository.model

data class AppPreferences(
    val appFirstTime: AppFirstTime = AppFirstTime.DefaultValue,
    val firstAppVersion: Int = 0,
    val nightTheme: Boolean = false,
    val fontSize: FontSize = FontSize.DefaultValue,
    @Deprecated(message = "Use deviceConfigEntity") val smartphoneScreen: Boolean = true,
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
    val displayDefaultAutocompletes: Boolean = true,
    val completedWithCheckbox: Boolean = true,
    val displayShoppingsProducts: DisplayProducts = DisplayProducts.DefaultValue,
    val enterToSaveProduct: Boolean = true,
    val coloredCheckbox: Boolean = false,
    val displayOtherFields: Boolean = true,
    val deviceConfig: DeviceConfig = DeviceConfig()
)