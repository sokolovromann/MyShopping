package ru.sokolovromann.myshopping.core.domain.model

import java.math.BigDecimal

data class Product(
    val uid: UID,
    val directory: ProductDirectory,
    val position: Position,
    val created: TimeInMillis,
    val lastModified: TimeInMillis,
    val status: ProductStatus,
    val priority: ProductPriority,
    val name: String,
    val quantity: ProductQuantity?,
    val unitPrice: BigDecimal?,
    val fullPrice: BigDecimal?,
    val discount: ProductDiscount?,
    val tax: Tax?,
    val cost: BigDecimal?,
    val note: String,
    val manufacturer: String,
    val brand: String,
    val size: String,
    val color: String
)