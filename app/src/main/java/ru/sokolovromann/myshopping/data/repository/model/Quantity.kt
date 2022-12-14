package ru.sokolovromann.myshopping.data.repository.model

import java.math.RoundingMode
import java.text.DecimalFormat

data class Quantity(
    val value: Float = 0f,
    val symbol: String = "",
    private val decimalFormat: DecimalFormat = DefaultDecimalFormat
) {

    companion object {
        val DefaultDecimalFormat = DecimalFormat().apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 5
            roundingMode = RoundingMode.HALF_UP
        }
    }

    fun valueToString(): String {
        return if (value % 1.0f == 0f) {
            String.format("%s", value.toLong())
        } else {
            value.toString()
        }
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