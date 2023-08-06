package ru.sokolovromann.myshopping.data.local.entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class AppPreferencesEntity(
    val appFirstTime: String = "",
    val firstAppVersion: Int = 0,
    val nightTheme: Boolean = false,
    val fontSize: String = "",

    @Transient
    @Deprecated(message = "Use deviceConfigEntity")
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
    val displayDefaultAutocompletes: Boolean = true,
    val completedWithCheckbox: Boolean = true,
    val displayShoppingsProducts: String = "",
    val enterToSaveProduct: Boolean = true,
    val coloredCheckbox: Boolean = false,
    val displayOtherFields: Boolean = true,

    @Transient
    val deviceConfigEntity: DeviceConfigEntity = DeviceConfigEntity()
)