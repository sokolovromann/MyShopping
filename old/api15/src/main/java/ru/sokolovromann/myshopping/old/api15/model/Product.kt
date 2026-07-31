package ru.sokolovromann.myshopping.old.api15.model

data class Product(
    val id: Int = 0,
    val position: Int = 0,
    val productUid: String = "",
    val shoppingUid: String = "",
    val lastModified: Long = 0L,
    val name: String = "",
    val quantity: Float = 0f,
    val quantitySymbol: String = "",
    val price: Float = 0f,
    val discount: Float = 0f,
    val discountAsPercent: Boolean = false,
    val taxRate: Float = 0f,
    val taxRateAsPercent: Boolean = false,
    val total: Float = 0f,
    val totalFormatted: Boolean = false,
    val note: String = "",
    val manufacturer: String = "",
    val brand: String = "",
    val size: String = "",
    val color: String = "",
    val provider: String = "",
    val completed: Boolean = false,
    val pinned: Boolean = false
)