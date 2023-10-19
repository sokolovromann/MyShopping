package ru.sokolovromann.myshopping.data.repository.model

import ru.sokolovromann.myshopping.data.model.AppConfig

data class Backup(
    val shoppingLists: List<ShoppingList> = listOf(),
    val products: List<Product> = listOf(),
    val autocompletes: List<Autocomplete> = listOf(),
    val appConfig: AppConfig = AppConfig(),
    val appVersion: Int = 0
)