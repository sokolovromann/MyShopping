package ru.sokolovromann.myshopping.utils.math

data class DecimalWithParams<P>(
    val decimal: Decimal,
    val params: P
)