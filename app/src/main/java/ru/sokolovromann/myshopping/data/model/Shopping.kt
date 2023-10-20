package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.data.repository.model.Id
import ru.sokolovromann.myshopping.data.repository.model.Money
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.repository.model.Sort

data class Shopping(
    val id: Int = Id.NO_ID,
    val position: Int = Id.FIRST_POSITION,
    val uid: String = Id.createUid(),
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