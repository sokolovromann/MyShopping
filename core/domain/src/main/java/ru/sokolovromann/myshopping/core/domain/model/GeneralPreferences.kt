package ru.sokolovromann.myshopping.core.domain.model

data class GeneralPreferences(
    val theme: Theme,
    val fontSize: FontSize,
    val moneyFormattingMode: MoneyFormattingMode,
    val currency: Currency
)