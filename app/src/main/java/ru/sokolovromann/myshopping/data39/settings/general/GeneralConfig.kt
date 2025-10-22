package ru.sokolovromann.myshopping.data39.settings.general

import ru.sokolovromann.myshopping.utils.DateTimeConfig

data class GeneralConfig(
    val theme: Theme,
    val fontSize: FontSize,
    val dateTime: DateTimeConfig,
    val money: MoneyConfig
)