package ru.sokolovromann.myshopping.data39.settings.general

import ru.sokolovromann.myshopping.utils.DateFormattingMode
import ru.sokolovromann.myshopping.utils.DateTimeConfig
import ru.sokolovromann.myshopping.utils.Decimal
import ru.sokolovromann.myshopping.utils.DecimalFractionDigits
import ru.sokolovromann.myshopping.utils.DecimalSign
import ru.sokolovromann.myshopping.utils.DecimalSignDisplaySide
import ru.sokolovromann.myshopping.utils.DecimalConfig
import ru.sokolovromann.myshopping.utils.TimeFormattingMode

object GeneralConfigDefaults {
    val THEME: Theme = Theme.Light
    val FONT_SIZE: FontSize = FontSize.Medium
    val DATE_TIME: DateTimeConfig = DateTimeConfig(
        dateFormattingMode = DateFormattingMode.MMMDDYYYY,
        timeFormattingMode = TimeFormattingMode.H12
    )
    val TAX_RATE: Decimal = Decimal.fromFloat(0f, DecimalConfig.Percent(DecimalFractionDigits.Fixed))
    val MONEY: MoneyConfig = MoneyConfig(
        decimalConfig = DecimalConfig.Money(
            DecimalSign("$", DecimalSignDisplaySide.Left),
            DecimalFractionDigits.Fixed
        ),
        taxRate = TAX_RATE
    )
}