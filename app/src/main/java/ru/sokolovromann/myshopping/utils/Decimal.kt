package ru.sokolovromann.myshopping.utils

import java.math.BigDecimal

class Decimal private constructor(private val bigDecimal: BigDecimal) {

    companion object {

        fun getZero(): Decimal {
            return Decimal(BigDecimal.ZERO)
        }

        fun getOne(): Decimal {
            return Decimal(BigDecimal.ONE)
        }

        fun createOrNull(value: BigDecimal?): Decimal? {
            return value?.let { Decimal(it) }
        }

        fun createOrNull(value: String?): Decimal? {
            return try {
                val bigDecimal = value?.toBigDecimal() ?: throw NullPointerException()
                Decimal(bigDecimal)
            } catch (_: Exception) { null }
        }

        fun createOrDefault(value: BigDecimal?, defaultValue: Decimal = getZero()): Decimal {
            return createOrNull(value) ?: defaultValue
        }

        fun createOrDefault(value: String?, defaultValue: Decimal = getZero()): Decimal {
            return createOrNull(value) ?: defaultValue
        }
    }

    fun plus(decimal: Decimal): Decimal {
        val other = decimal.toString().toBigDecimal()
        val newValue = bigDecimal.plus(other)
        return Decimal(newValue)
    }

    fun minus(decimal: Decimal): Decimal {
        val other = decimal.toString().toBigDecimal()
        val newValue = bigDecimal.minus(other)
        return Decimal(newValue)
    }

    fun multiply(decimal: Decimal): Decimal {
        val other = decimal.toString().toBigDecimal()
        val newValue = bigDecimal.multiply(other)
        return Decimal(newValue)
    }

    fun divide(decimal: Decimal): Decimal {
        val other = decimal.toString().toBigDecimal()
        val newValue = bigDecimal.divide(other)
        return Decimal(newValue)
    }

    fun getBigDecimal(): BigDecimal {
        return bigDecimal
    }

    fun getString(): String {
        return bigDecimal.toString()
    }

    override fun toString(): String {
        return getString()
    }
}