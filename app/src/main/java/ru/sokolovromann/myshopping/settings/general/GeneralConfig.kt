package ru.sokolovromann.myshopping.settings.general

data class GeneralConfig(
    val theme: Theme,
    val fontSize: FontSize,
    val dateTime: DateTimeConfig,
    val money: MoneyConfig
)