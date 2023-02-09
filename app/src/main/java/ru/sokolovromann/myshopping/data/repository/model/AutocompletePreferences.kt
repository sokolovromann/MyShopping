package ru.sokolovromann.myshopping.data.repository.model

data class AutocompletePreferences(
    val currency: Currency = Currency(),
    val fontSize: FontSize = FontSize.DefaultValue,
    val firstLetterUppercase: Boolean = true,
    val screenSize: ScreenSize = ScreenSize.DefaultValue
)