package ru.sokolovromann.myshopping.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

class Decimal private constructor(
    private val bigDecimal: BigDecimal,
    private val config: DecimalConfig
) {

    companion object {

        fun fromFloat(value: Float, config: DecimalConfig): Decimal {
            return Decimal(value.toBigDecimal(), config)
        }

        fun fromString(value: String, config: DecimalConfig): Decimal? {
            return value.toBigDecimalOrNull()?.let { Decimal(it, config) }
        }
    }

    fun newValue(value: Float): Decimal {
        return Decimal(value.toBigDecimal(), config)
    }

    fun newValue(value: String): Decimal? {
        return value.toBigDecimalOrNull()?.let { Decimal(it, config) }
    }

    fun plus(value: Float): Decimal {
        val newValue = bigDecimal.plus(value.toBigDecimal())
        return Decimal(newValue, config)
    }

    fun minus(value: Float): Decimal {
        val newValue = bigDecimal.minus(value.toBigDecimal())
        return Decimal(newValue, config)
    }

    fun multiply(value: Float): Decimal {
        val newValue = bigDecimal.multiply(value.toBigDecimal())
        return Decimal(newValue, config)
    }

    fun divide(value: Float): Decimal {
        val newValue = bigDecimal.divide(value.toBigDecimal())
        return Decimal(newValue, config)
    }

    fun getFloat(): Float {
        return getFormattedString().toFloat()
    }

    fun getRawString(): String {
        return bigDecimal.toString()
    }

    fun getFormattedString(): String {
        val spaceChar = Char(code = 32)
        val periodChar = Char(code = 46)
        val noText = ""
        val decimalFormat = createDecimalFormat().apply {
            decimalFormatSymbols = decimalFormatSymbols.apply {
                groupingSeparator = spaceChar
                decimalSeparator = periodChar
                naN = noText
            }
        }
        return decimalFormat.format(bigDecimal)
            .replace(spaceChar.toString(), noText)
    }

    fun getDisplay(): String {
        val value = createDecimalFormat().format(bigDecimal)
        val sign = config.sign
        return when (sign.displaySide) {
            DecimalSignDisplaySide.Left -> "${sign.symbol}$value"
            DecimalSignDisplaySide.Right -> "$value${sign.symbol}"
        }
    }

    fun getSymbol(): String {
        return config.sign.symbol
    }

    override fun toString(): String {
        return getDisplay()
    }

    private fun createDecimalFormat(): DecimalFormat {
        return DecimalFormat().apply {
            roundingMode = RoundingMode.HALF_UP
            minimumFractionDigits = when (config.fractionDigits) {
                DecimalFractionDigits.Fixed -> {
                    when (config) {
                        is DecimalConfig.Unspecified -> 5
                        is DecimalConfig.Percent -> 2
                        is DecimalConfig.Quantity -> 3
                        is DecimalConfig.Money -> 2
                    }
                }
                DecimalFractionDigits.Unfixed -> 0
            }
            maximumFractionDigits = when (config) {
                is DecimalConfig.Unspecified -> 5
                is DecimalConfig.Percent -> 2
                is DecimalConfig.Quantity -> 3
                is DecimalConfig.Money -> 2
            }
        }
    }
}