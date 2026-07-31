package ru.sokolovromann.myshopping.old.api15.model

data class Autocomplete(
    val id: Int = 0,
    val uid: String = "",
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
    val manufacturer: String = "",
    val brand: String = "",
    val size: String = "",
    val color: String = "",
    val provider: String = "",
    val personal: Boolean = true,
    val language: String = ""
)