package ru.sokolovromann.myshopping.data.local.entity

data class AutocompletePreferencesEntity(
    val currency: String = "",
    val currencyDisplayToLeft: Boolean = false,
    val fontSize: String = "",
    val firstLetterUppercase: Boolean = false,
    val screenSize: String = ""
)