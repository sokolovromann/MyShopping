package ru.sokolovromann.myshopping.core.domain.utils

import ru.sokolovromann.myshopping.core.domain.model.ProductQuantity
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

object QuantityUtils {

    fun format(quantity: ProductQuantity): String {
        val formatted = createDefaultDecimalFormat().format(quantity.number)
        return "$formatted ${quantity.measurementUnit}"
    }

    fun formatToPlainString(bigDecimal: BigDecimal): String {
        val spaceChar = Char(code = 32)
        val periodChar = Char(code = 46)
        val noText = ""
        val decimalFormat = createDefaultDecimalFormat().apply {
            decimalFormatSymbols = decimalFormatSymbols.apply {
                groupingSeparator = spaceChar
                decimalSeparator = periodChar
                naN = noText
            }
        }
        return decimalFormat.format(bigDecimal)
            .replace(spaceChar.toString(), noText)
    }

    private fun createDefaultDecimalFormat() = DecimalFormat().apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 3
        roundingMode = RoundingMode.HALF_UP
    }
}