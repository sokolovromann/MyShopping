package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.data.utils.formattedValueWithoutSeparators
import java.math.BigDecimal
import java.text.DecimalFormat

data class Money(
    val value: BigDecimal = BigDecimal.ZERO,
    val currency: Currency = Currency(),
    val asPercent: Boolean = false,
    val decimalFormat: DecimalFormat = UserPreferencesDefaults.getMoneyDecimalFormat()
) {

    private val _decimalFormat = if (value.toFloat() % 1f == 0f) {
        decimalFormat
    } else {
        UserPreferencesDefaults.getMoneyDecimalFormat()
    }

    fun getFormattedValue(): String {
        return _decimalFormat.format(value)
    }

    fun getFormattedValueWithoutSeparators(): String {
        return _decimalFormat.formattedValueWithoutSeparators(value)
    }

    fun getDisplayValue(): String {
        return if (asPercent) {
            "${getFormattedValue()}%"
        } else {
            if (currency.displayToLeft) {
                "${currency.symbol}${getFormattedValue()}"
            } else {
                "${getFormattedValue()}${currency.symbol}"
            }
        }
    }

    fun calculateValueFromPercent(money: BigDecimal): BigDecimal {
        val bigDecimal = BigDecimal(value.toString())
        return if (asPercent) {
            val percent = 100f.toBigDecimal()
            val percentToDecimal = bigDecimal.divide(percent)
            money.multiply(percentToDecimal)
        } else {
            bigDecimal
        }
    }

    fun isEmpty(): Boolean {
        return value.toFloat() <= 0f
    }

    fun isNotEmpty(): Boolean {
        return value.toFloat() > 0f
    }

    override fun toString(): String {
        return getDisplayValue()
    }
}