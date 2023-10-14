package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.app.AppLocale
import ru.sokolovromann.myshopping.data.repository.model.Id
import ru.sokolovromann.myshopping.data.repository.model.Money
import ru.sokolovromann.myshopping.data.repository.model.Quantity
import ru.sokolovromann.myshopping.data.repository.model.Time

data class Autocomplete(
    val id: Int = Id.NO_ID,
    val position: Int = id,
    val uid: String = Id.createUid(),
    val lastModified: Time = Time.getCurrentTime(),
    val name: String = "",
    val quantity: Quantity = Quantity(),
    val price: Money = Money(),
    val discount: Money = Money(),
    val taxRate: Money = Money(),
    val total: Money = Money(),
    val manufacturer: String = "",
    val brand: String = "",
    val size: String = "",
    val color: String = "",
    val provider: String = "",
    val personal: Boolean = true,
    val language: String = AppLocale.getCurrentLanguage()
)