package ru.sokolovromann.myshopping.data.model

data class Product(
    val id: Int = IdDefaults.NO_ID,
    val position: Int = IdDefaults.FIRST_POSITION,
    val productUid: String = IdDefaults.createUid(),
    val shoppingUid: String = IdDefaults.NO_UID,
    val lastModified: DateTime = DateTime.getCurrentDateTime(),
    val name: String = "",
    val quantity: Quantity = Quantity(),
    val price: Money = Money(),
    val discount: Money = Money(),
    val taxRate: Money = Money(),
    val total: Money = Money(),
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