package ru.sokolovromann.myshopping.data.local.entity

data class CodeVersion14UserPreferencesEntity(
    val firstOpened: Boolean? = null,
    val currency: String? = null,
    val displayCurrencyToLeft: Boolean? = null,
    val taxRate: Float? = null,
    val titleFontSize: Int? = null,
    val bodyFontSize: Int? = null,
    val firstLetterUppercase: Boolean? = null,
    val columnCount: Int? = null,
    val sort: Int? = null,
    val displayMoney: Boolean? = null,
    val displayTotal: Int? = null,
    val editProductAfterCompleted: Boolean? = null,
    val saveProductToAutocompletes: Boolean? = null
)