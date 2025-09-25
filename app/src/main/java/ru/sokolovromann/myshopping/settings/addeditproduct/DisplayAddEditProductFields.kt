package ru.sokolovromann.myshopping.settings.addeditproduct

data class DisplayAddEditProductFields(
    val name: Boolean,
    val image: Boolean,
    val manufacturer: Boolean,
    val brand: Boolean,
    val size: Boolean,
    val color: Boolean,
    val quantity: Boolean,
    val minusAndPlusOneQuantity: Boolean,
    val price: Boolean,
    val discount: Boolean,
    val taxRate: Boolean,
    val cost: Boolean,
    val note: Boolean,
    val id: Boolean,
    val created: Boolean,
    val lastModified: Boolean
)