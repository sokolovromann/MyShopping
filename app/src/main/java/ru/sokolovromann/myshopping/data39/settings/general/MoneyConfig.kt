package ru.sokolovromann.myshopping.data39.settings.general

import ru.sokolovromann.myshopping.utils.Decimal
import ru.sokolovromann.myshopping.utils.DecimalFormattingMode

data class MoneyConfig(
    val formattingMode: DecimalFormattingMode.MoneyParams,
    val currency: Currency,
    val taxRate: Decimal
)