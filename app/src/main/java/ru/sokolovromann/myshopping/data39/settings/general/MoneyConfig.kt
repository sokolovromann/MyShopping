package ru.sokolovromann.myshopping.data39.settings.general

import ru.sokolovromann.myshopping.utils.Decimal
import ru.sokolovromann.myshopping.utils.DecimalConfig

data class MoneyConfig(
    val decimalConfig: DecimalConfig.Money,
    val taxRate: Decimal
)