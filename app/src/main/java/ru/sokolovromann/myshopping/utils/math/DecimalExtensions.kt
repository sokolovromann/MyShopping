package ru.sokolovromann.myshopping.utils.math

object DecimalExtensions {

    fun String.toDecimal(): Decimal {
        return Decimal(this)
    }

    fun Float.toDecimal(): Decimal {
        return Decimal(this)
    }
}