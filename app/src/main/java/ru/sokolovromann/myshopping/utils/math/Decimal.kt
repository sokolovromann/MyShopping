package ru.sokolovromann.myshopping.utils.math

import java.math.BigDecimal

class Decimal(private val value: String) {
    constructor(value: Float) : this(value.toString())

    companion object {
        val ZERO: Decimal = Decimal(0f)
        val ONE: Decimal = Decimal(1f)
    }

    fun plus(other: Decimal): Decimal {
        val newValue = toBigDecimalOrZero()
            .plus(other.toBigDecimalOrZero())
            .toPlainString()
        return Decimal(newValue)
    }

    fun minus(other: Decimal): Decimal {
        val newValue = toBigDecimalOrZero()
            .minus(other.toBigDecimalOrZero())
            .toPlainString()
        return Decimal(newValue)
    }

    fun multiply(other: Decimal): Decimal {
        val newValue = toBigDecimalOrZero()
            .multiply(other.toBigDecimalOrZero())
            .toPlainString()
        return Decimal(newValue)
    }

    fun divide(other: Decimal): Decimal {
        val newValue = toBigDecimalOrZero()
            .divide(other.toBigDecimalOrZero())
            .toPlainString()
        return Decimal(newValue)
    }

    fun toFloatOrNull(): Float? {
        return value.toFloatOrNull()
    }

    fun toFloatOrZero(): Float {
        return toFloatOrNull() ?: 0f
    }

    fun toBigDecimalOrNull(): BigDecimal? {
        return value.toBigDecimalOrNull()
    }

    fun toBigDecimalOrZero(): BigDecimal {
        return toBigDecimalOrNull() ?: BigDecimal.ZERO
    }

    override fun toString(): String {
        return value
    }
}