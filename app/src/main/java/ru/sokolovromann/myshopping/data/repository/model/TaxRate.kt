package ru.sokolovromann.myshopping.data.repository.model

import java.math.RoundingMode
import java.text.DecimalFormat

@Deprecated("Use Money")
data class TaxRate(
    val value: Float = 0f,
    val asPercent: Boolean = true,
    val percent: String = "%",
    val currency: Currency = Currency(),
    private val decimalFormat: DecimalFormat = DefaultDecimalFormat
) {

    companion object {
        val DefaultDecimalFormat = DecimalFormat().apply {
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

    fun formatValueWithPercentOrCurrency(): String {
        return if (asPercent) {
            "${formatValue()} $percent"
        } else {
            if (currency.displayToLeft) {
                "${currency.symbol}${formatValue()}"
            } else {
                "${formatValue()}${currency.symbol}"
            }
        }
    }

    fun calculate(money: Money): Money {
        if (money.isEmpty()) {
            return Money(0f, money.currency)
        }

        return Money(calculate(money.value), money.currency)
    }

    fun calculate(money: Float): Float {
        return if (asPercent) money * (value / 100) else value
    }

    fun isEmpty(): Boolean {
        return value <= 0f
    }

    fun isNotEmpty(): Boolean {
        return value > 0f
    }

    override fun toString(): String {
        return formatValueWithPercentOrCurrency()
    }
}