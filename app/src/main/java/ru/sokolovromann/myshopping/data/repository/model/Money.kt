package ru.sokolovromann.myshopping.data.repository.model

import java.math.RoundingMode
import java.text.DecimalFormat

@Deprecated("Use AppFloat")
data class Money(
    val value: Float = 0f,
    val currency: Currency = Currency(),
    private val decimalFormat: DecimalFormat = DefaultDecimalFormat
) {

    companion object {
        val DefaultDecimalFormat: DecimalFormat = DecimalFormat().apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
            roundingMode = RoundingMode.HALF_UP
        }
    }

    fun valueToString(): String {
        val format = decimalFormat
        return format.valueToString(value)
    }

    fun formatValue(): String {
        return decimalFormat.format(value)
    }

    fun formatValueWithCurrency(): String {
        return if (currency.displayToLeft) {
            "${currency.symbol}${formatValue()}"
        } else {
            "${formatValue()}${currency.symbol}"
        }
    }

    fun isEmpty(): Boolean {
        return value <= 0f
    }

    fun isNotEmpty(): Boolean {
        return value > 0f
    }

    override fun toString(): String {
        return formatValueWithCurrency()
    }
}