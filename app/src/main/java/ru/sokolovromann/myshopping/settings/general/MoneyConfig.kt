package ru.sokolovromann.myshopping.settings.general

import ru.sokolovromann.myshopping.utils.DecimalFormattingMode

data class MoneyConfig(
    val formattingMode: DecimalFormattingMode.MoneyParams,
    val currency: Currency,
    val taxRate: TaxRate
)