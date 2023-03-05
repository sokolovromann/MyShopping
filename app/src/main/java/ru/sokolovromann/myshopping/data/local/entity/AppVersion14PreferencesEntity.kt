package ru.sokolovromann.myshopping.data.local.entity

data class AppVersion14PreferencesEntity(
    val firstOpened: Boolean = false,
    val currency: String = "",
    val displayCurrencyToLeft: Boolean = false,
    val taxRate: Float = 0f,
    val titleFontSize: Int = 0,
    val bodyFontSize: Int = 0,
    val firstLetterUppercase: Boolean = false,
    val columnCount: Int = 0,
    val sort: Int = 0,
    val displayMoney: Boolean = false,
    val displayTotal: Int = 0,
    val editProductAfterCompleted: Boolean = false,
    val saveProductToAutocompletes: Boolean = false,
)