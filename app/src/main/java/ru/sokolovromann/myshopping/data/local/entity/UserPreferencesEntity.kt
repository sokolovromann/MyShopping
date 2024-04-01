package ru.sokolovromann.myshopping.data.local.entity

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferencesEntity(
    val nightTheme: Boolean? = null,
    val appFontSize: String? = null,
    val widgetFontSize: String? = null,
    val shoppingsMultiColumns: Boolean? = null,
    val shoppingsSortBy: String? = null,
    val shoppingsSortAscending: Boolean? = null,
    val shoppingsSortFormatted: Boolean? = null,
    val productsMultiColumns: Boolean? = null,
    val displayCompleted: String? = null,
    val strikethroughCompletedProducts: Boolean? = null,
    val displayTotal: String? = null,
    val displayOtherFields: Boolean? = null,
    val displayLongTotal: Boolean? = null,
    val coloredCheckbox: Boolean? = null,
    val displayShoppingsProducts: String? = null,
    val purchasesSeparator: String? = null,
    val editProductAfterCompleted: Boolean? = null,
    val lockProductElement: String? = null,
    val completedWithCheckbox: Boolean? = null,
    val enterToSaveProduct: Boolean? = null,
    val displayDefaultAutocompletes: Boolean? = null,
    val maxAutocompletesNames: Int? = null,
    val maxAutocompletesQuantities: Int? = null,
    val maxAutocompletesMoneys: Int? = null,
    val maxAutocompletesOthers: Int? = null,
    val saveProductToAutocompletes: Boolean? = null,
    val displayMoney: Boolean? = null,
    val currency: String? = null,
    val displayCurrencyToLeft: Boolean? = null,
    val taxRate: Float? = null,
    val taxRateAsPercent: Boolean? = null,
    val minMoneyFractionDigits: Int? = null,
    val minQuantityFractionDigits: Int? = null,
    val maxMoneyFractionDigits: Int? = null,
    val maxQuantityFractionDigits: Int? = null
)