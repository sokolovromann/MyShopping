package ru.sokolovromann.myshopping.core.domain.model

import java.math.BigDecimal

data class ProductDiscount(
    val money: BigDecimal,
    val measurementUnit: DiscountMeasurementUnit
)