package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.data.repository.model.Id
import ru.sokolovromann.myshopping.data.repository.model.Money
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.repository.model.Sort
import ru.sokolovromann.myshopping.data.repository.model.Time

data class Shopping(
    val id: Int = Id.NO_ID,
    val position: Int = Id.FIRST_POSITION,
    val uid: String = Id.createUid(),
    val lastModified: Time = Time.getCurrentTime(),
    val name: String = "",
    val reminder: Time? = null,
    val total: Money = Money(),
    val totalFormatted: Boolean = false,
    val budget: Money = Money(),
    val location: ShoppingLocation = ShoppingLocation.DefaultValue,
    val sort: Sort = Sort(),
    val sortFormatted: Boolean = false,
    val pinned: Boolean = false
)