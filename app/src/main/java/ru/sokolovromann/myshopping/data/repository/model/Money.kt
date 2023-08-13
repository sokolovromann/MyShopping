package ru.sokolovromann.myshopping.data.repository.model

import java.text.DecimalFormat

data class Money(
    val value: Float = 0f,
    val currency: Currency = Currency(),
    val asPercent: Boolean = false,
    val decimalFormat: DecimalFormat = UserPreferencesDefaults.getMoneyDecimalFormat()
) {

    fun getFormattedValue(): String {
        return decimalFormat.format(value)
    }

    fun getFormattedValueWithoutSeparators(): String {
        return decimalFormat.valueToString(value)
    }

    fun getDisplayValue(): String {
        return if (asPercent) {
            "${getFormattedValue()} %"
        } else {
            if (currency.displayToLeft) {
                "${currency.symbol}${getFormattedValue()}"
            } else {
                "${getFormattedValue()}${currency.symbol}"
            }
        }
    }

    fun calculateValueFromPercent(money: Float): Float {
        return if (asPercent) money * (value / 100) else value
    }

    fun isEmpty(): Boolean {
        return value <= 0f
    }

    fun isNotEmpty(): Boolean {
        return value > 0f
    }

    override fun toString(): String {
        return getDisplayValue()
    }
}