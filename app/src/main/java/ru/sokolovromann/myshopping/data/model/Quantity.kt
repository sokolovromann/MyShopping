package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.data.utils.formattedValueWithoutSeparators
import java.math.BigDecimal
import java.text.DecimalFormat

data class Quantity(
    val value: BigDecimal = BigDecimal.ZERO,
    val symbol: String = "",
    val decimalFormat: DecimalFormat = UserPreferencesDefaults.getQuantityDecimalFormat()
) {

    fun getFormattedValue(): String {
        return decimalFormat.format(value)
    }

    fun getFormattedValueWithoutSeparators(): String {
        val format = decimalFormat
        return format.formattedValueWithoutSeparators(value)
    }

    fun getDisplayValue(): String {
        return if (symbol.isEmpty()) {
            getFormattedValue()
        } else {
            "${getFormattedValue()} $symbol"
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