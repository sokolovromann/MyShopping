package ru.sokolovromann.myshopping.data39.settings.addeditproduct

data class DisplayAddEditProductFields(
    val name: Boolean,
    val image: Boolean,
    val manufacturer: Boolean,
    val brand: Boolean,
    val size: Boolean,
    val color: Boolean,
    val quantity: Boolean,
    val plusMinusOneQuantity: Boolean,
    val unitPrice: Boolean,
    val discount: Boolean,
    val taxRate: Boolean,
    val cost: Boolean,
    val note: Boolean,
    val id: Boolean,
    val created: Boolean,
    val lastModified: Boolean
)