package ru.sokolovromann.myshopping.utils

sealed class DecimalConfig(
    val sign: DecimalSign,
    val fractionDigits: DecimalFractionDigits
) {

    data object Unspecified : DecimalConfig(
        sign = DecimalSign("", DecimalSignDisplaySide.Left),
        fractionDigits = DecimalFractionDigits.Unfixed
    )

    data class Percent(private val percentFractionDigits: DecimalFractionDigits) : DecimalConfig(
        sign = DecimalSign(" %", DecimalSignDisplaySide.Right),
        fractionDigits = percentFractionDigits
    )

    data class Quantity(private val quantitySign: String) : DecimalConfig(
        sign = DecimalSign(" $quantitySign", DecimalSignDisplaySide.Right),
        fractionDigits = DecimalFractionDigits.Unfixed
    )

    data class Money(
        private val moneySign: DecimalSign,
        private val moneyFractionDigits: DecimalFractionDigits
    ) : DecimalConfig(moneySign, moneyFractionDigits)
}