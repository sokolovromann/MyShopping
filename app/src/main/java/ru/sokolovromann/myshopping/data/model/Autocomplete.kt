package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.app.AppLocale

data class Autocomplete(
    val id: Int = IdDefaults.NO_ID,
    val position: Int = IdDefaults.FIRST_POSITION,
    val uid: String = IdDefaults.createUid(),
    val lastModified: DateTime = DateTime.getCurrentDateTime(),
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