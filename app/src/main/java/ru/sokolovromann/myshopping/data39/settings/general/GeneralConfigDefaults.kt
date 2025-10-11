package ru.sokolovromann.myshopping.data39.settings.general

import ru.sokolovromann.myshopping.utils.DateFormattingMode
import ru.sokolovromann.myshopping.utils.Decimal
import ru.sokolovromann.myshopping.utils.DecimalFormattingMode
import ru.sokolovromann.myshopping.utils.TimeFormattingMode

object GeneralConfigDefaults {
    val THEME: Theme = Theme.Light
    val FONT_SIZE: FontSize = FontSize.Medium
    val DATE_TIME: DateTimeConfig = DateTimeConfig(
        dateFormattingMode = DateFormattingMode.MMMDDYYYY,
        timeFormattingMode = TimeFormattingMode.H12
    )
    val MONEY: MoneyConfig = MoneyConfig(
        formattingMode = DecimalFormattingMode.MoneyParams.Advanced,
        currency = Currency("$", CurrencyDisplaySide.Left),
        taxRate = Decimal.Companion.getZero()
    )
}