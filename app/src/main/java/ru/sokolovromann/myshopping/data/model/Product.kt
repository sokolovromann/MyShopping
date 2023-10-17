package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.data.repository.model.Id
import ru.sokolovromann.myshopping.data.repository.model.Money
import ru.sokolovromann.myshopping.data.repository.model.Quantity
import ru.sokolovromann.myshopping.data.repository.model.Time

data class Product(
    val id: Int = Id.NO_ID,
    val position: Int = Id.FIRST_POSITION,
    val productUid: String = Id.createUid(),
    val shoppingUid: String = Id.NO_UID,
    val lastModified: Time = Time.getCurrentTime(),
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