package ru.sokolovromann.myshopping.core.domain.model

import java.math.BigDecimal

data class CartTotal(
    val money: BigDecimal,
    val filterByStatus: FilterProductsByStatus
)