package ru.sokolovromann.myshopping.core.domain.model

import java.math.BigDecimal

data class CartDiscount(
    val money: BigDecimal,
    val measurementUnit: DiscountMeasurementUnit,
    val filterByStatus: FilterProductsByStatus
)