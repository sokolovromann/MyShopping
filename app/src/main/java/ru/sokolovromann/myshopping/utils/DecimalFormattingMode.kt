package ru.sokolovromann.myshopping.utils

sealed class DecimalFormattingMode {

    enum class MoneyParams {

        Simple,

        Advanced;
    }

    data object Percent : DecimalFormattingMode()

    data object Quantity : DecimalFormattingMode()

    data class Money(val params: MoneyParams) : DecimalFormattingMode()
}