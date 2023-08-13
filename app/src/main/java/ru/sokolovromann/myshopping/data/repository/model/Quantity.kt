package ru.sokolovromann.myshopping.data.repository.model

import java.text.DecimalFormat

data class Quantity(
    val value: Float = 0f,
    val symbol: String = "",
    val decimalFormat: DecimalFormat = UserPreferencesDefaults.getQuantityDecimalFormat()
) {

    fun valueToString(): String {
        val format = decimalFormat
        return format.valueToString(value)
    }

    fun formatValue(): String {
        return decimalFormat.format(value)
    }

    fun formatValueWithSymbol(): String {
        return "${formatValue()} $symbol"
    }

    fun isEmpty(): Boolean {
        return value <= 0f
    }

    fun isNotEmpty(): Boolean {
        return value > 0f
    }

    override fun toString(): String {
        return formatValueWithSymbol()
    }
}