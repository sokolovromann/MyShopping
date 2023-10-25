package ru.sokolovromann.myshopping.data.model

data class Shopping(
    val id: Int = IdDefaults.NO_ID,
    val position: Int = IdDefaults.FIRST_POSITION,
    val uid: String = IdDefaults.createUid(),
    val lastModified: DateTime = DateTime.getCurrentDateTime(),
    val name: String = "",
    val reminder: DateTime? = null,
    val total: Money = Money(),
    val totalFormatted: Boolean = false,
    val budget: Money = Money(),
    val location: ShoppingLocation = ShoppingLocation.DefaultValue,
    val sort: Sort = Sort(),
    val sortFormatted: Boolean = false,
    val pinned: Boolean = false
)