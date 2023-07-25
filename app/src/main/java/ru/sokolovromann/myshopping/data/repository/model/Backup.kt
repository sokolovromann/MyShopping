package ru.sokolovromann.myshopping.data.repository.model

data class Backup(
    val shoppingLists: List<ShoppingList> = listOf(),
    val products: List<Product> = listOf(),
    val autocompletes: List<Autocomplete> = listOf(),
    val preferences: AppPreferences = AppPreferences()
)