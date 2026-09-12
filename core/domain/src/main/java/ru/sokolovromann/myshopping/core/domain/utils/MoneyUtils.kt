package ru.sokolovromann.myshopping.core.domain.utils

import ru.sokolovromann.myshopping.core.domain.model.Currency
import ru.sokolovromann.myshopping.core.domain.model.DiscountMeasurementUnit
import ru.sokolovromann.myshopping.core.domain.model.MoneyFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.ProductDiscount
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

object MoneyUtils {

    fun format(
        bigDecimal: BigDecimal,
        currency: Currency,
        formattingMode: MoneyFormattingMode
    ): String {
        val formatted = createDefaultDecimalFormat(formattingMode).format(bigDecimal)
        return when (currency) {
            is Currency.Left -> "${currency.currencySign}$formatted"
            is Currency.Right -> "$formatted${currency.currencySign}"
        }
    }

    fun format(
        discount: ProductDiscount,
        currency: Currency,
        formattingMode: MoneyFormattingMode
    ): String {
        val formatted = createDefaultDecimalFormat(formattingMode).format(discount.money)
        return when (discount.measurementUnit) {
            DiscountMeasurementUnit.Percent -> "$formatted %"
            DiscountMeasurementUnit.Money -> when (currency) {
                is Currency.Left -> "${currency.currencySign}$formatted"
                is Currency.Right -> "$formatted${currency.currencySign}"
            }
        }
    }

    fun formatToPlainString(
        bigDecimal: BigDecimal,
        formattingMode: MoneyFormattingMode
    ): String {
        val spaceChar = Char(code = 32)
        val periodChar = Char(code = 46)
        val noText = ""
        val decimalFormat = createDefaultDecimalFormat(formattingMode).apply {
            decimalFormatSymbols = decimalFormatSymbols.apply {
                groupingSeparator = spaceChar
                decimalSeparator = periodChar
                naN = noText
            }
        }
        return decimalFormat.format(bigDecimal)
            .replace(spaceChar.toString(), noText)
    }

    private fun createDefaultDecimalFormat(
        formattingMode: MoneyFormattingMode
    ) = DecimalFormat().apply {
        minimumFractionDigits = when (formattingMode) {
            MoneyFormattingMode.Simple -> 2
            MoneyFormattingMode.Advanced -> 0
        }
        maximumFractionDigits = 2
        roundingMode = RoundingMode.HALF_UP
    }
}