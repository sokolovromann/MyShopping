package ru.sokolovromann.myshopping.data.repository.model

import java.text.DecimalFormat

data class AppFloat(
    val value: Float,
    val type: AppFloatType,
    val decimalFormat: DecimalFormat = DecimalFormat()
) : AppNumber {

    fun getFormattedValue(): String {
        return decimalFormat.format(value)
    }

    fun getFormattedWithoutSeparatorsValue(): String {
        return decimalFormat.valueToString(value)
    }

    override fun getDisplayValue(): String {
        return when (type) {
            is AppFloatType.Money -> {
                val currency = type.currency
                if (currency.displayToLeft) {
                    "${currency.symbol}${getFormattedValue()}"
                } else {
                    "${getFormattedValue()}${currency.symbol}"
                }
            }

            is AppFloatType.Quantity -> "$value ${type.symbol}"

            AppFloatType.Percent -> "${getFormattedValue()} %"
        }
    }

    override fun isEmpty(): Boolean {
        return value <= 0f
    }

    override fun isNotEmpty(): Boolean {
        return value > 0f
    }

    override fun toString(): String {
        return getDisplayValue()
    }
}
