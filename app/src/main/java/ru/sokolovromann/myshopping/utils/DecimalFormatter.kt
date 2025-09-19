package ru.sokolovromann.myshopping.utils

import java.math.RoundingMode
import java.text.DecimalFormat

class DecimalFormatter(private val formattingMode: DecimalFormattingMode) {

    private val noText: String = ""

    fun getDisplay(decimal: Decimal): String {
        return try {
            createDecimalFormat().format(decimal.getBigDecimal())
        } catch (_: Exception) { noText }
    }

    fun getString(decimal: Decimal): String {
        return try {
            val spaceChar = Char(code = 32)
            val periodChar = Char(code = 46)
            val decimalFormat = createDecimalFormat().apply {
                decimalFormatSymbols = decimalFormatSymbols.apply {
                    minusSign = spaceChar
                    groupingSeparator = spaceChar
                    decimalSeparator = periodChar
                    naN = noText
                }
            }
            decimalFormat.format(decimal.getBigDecimal())
                .replace(spaceChar.toString(), noText)
        } catch (_: Exception) { noText }
    }

    override fun toString(): String {
        return noText
    }

    private fun createDecimalFormat(): DecimalFormat {
        return DecimalFormat().apply {
            roundingMode = RoundingMode.HALF_UP
            minimumFractionDigits = when (formattingMode) {
                is DecimalFormattingMode.Percent -> 2
                is DecimalFormattingMode.Quantity -> 0
                is DecimalFormattingMode.Money -> {
                    when (formattingMode.params) {
                        DecimalFormattingMode.MoneyParams.Simple -> 2
                        DecimalFormattingMode.MoneyParams.Advanced -> 0
                    }
                }
            }
            maximumFractionDigits = when (formattingMode) {
                is DecimalFormattingMode.Percent -> 2
                is DecimalFormattingMode.Quantity -> 3
                is DecimalFormattingMode.Money -> 2
            }
        }
    }
}