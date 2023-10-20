package ru.sokolovromann.myshopping.data.repository.model

import ru.sokolovromann.myshopping.app.AppLocale
import ru.sokolovromann.myshopping.data.model.Money
import java.util.UUID

@Deprecated("Use model/Autocomplete")
data class Autocomplete(
    val id: Int = 0,
    val uid: String = UUID.randomUUID().toString(),
    val created: Long = System.currentTimeMillis(),
    val lastModified: Long = created,
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