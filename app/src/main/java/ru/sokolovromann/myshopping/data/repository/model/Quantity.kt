package ru.sokolovromann.myshopping.data.repository.model

import java.text.DecimalFormat

data class Quantity(
    val value: Float = 0f,
    val symbol: String = "",
    val decimalFormat: DecimalFormat = UserPreferencesDefaults.getQuantityDecimalFormat()
) {

    fun getFormattedValue(): String {
        return decimalFormat.format(value)
    }

    fun getFormattedValueWithoutSeparators(): String {
        val format = decimalFormat
        return format.formatValueWithoutSeparators(value)
    }

    fun getDisplayValue(): String {
        return "${getFormattedValue()} $symbol"
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