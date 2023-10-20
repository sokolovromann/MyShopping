package ru.sokolovromann.myshopping.data.utils

import java.text.DecimalFormat

fun DecimalFormat.formattedValueWithoutSeparators(value: Float): String {
    val spaceCode = 32
    val periodCode = 46
    val formatSymbols = decimalFormatSymbols.apply {
        minusSign = Char(spaceCode)
        groupingSeparator = Char(spaceCode)
        decimalSeparator = Char(periodCode)
        naN = ""
    }
    return apply { decimalFormatSymbols = formatSymbols }
        .format(value)
        .replace(" ", "")
}

fun DecimalFormat.displayZerosAfterDecimal(): Boolean {
    return minimumFractionDigits > 0
}