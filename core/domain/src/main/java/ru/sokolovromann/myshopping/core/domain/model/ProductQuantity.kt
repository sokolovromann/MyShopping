package ru.sokolovromann.myshopping.core.domain.model

import java.math.BigDecimal

data class ProductQuantity(
    val number: BigDecimal,
    val measurementUnit: String
)