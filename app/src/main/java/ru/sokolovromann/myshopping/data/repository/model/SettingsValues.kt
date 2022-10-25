package ru.sokolovromann.myshopping.data.repository.model

data class SettingsValues(
    val nightTheme: Boolean = false,
    val currency: Currency = Currency(),
    val taxRate: TaxRate = TaxRate(),
    val fontSize: FontSize = FontSize.DefaultValue,
    val firstLetterUppercase: Boolean = true,
    val displayMoney: Boolean = true,
    val shoppingsMultiColumns: Boolean = false,
    val productsMultiColumns: Boolean = false,
    val productsDisplayAutocomplete: DisplayAutocomplete = DisplayAutocomplete.DefaultValue,
    val productsEditCompleted: Boolean = false,
    val productsAddLastProduct: Boolean = true,
    val developerName: String = "",
    val developerEmail: String = "",
    val appVersion: String = "",
    val appGithubLink: String = ""
)