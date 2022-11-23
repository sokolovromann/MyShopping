package ru.sokolovromann.myshopping.data.repository.model

data class AppVersion14(
    val shoppingLists: List<ShoppingList> = listOf(),
    val autocompletes: List<Autocomplete> = listOf(),
    val preferences: AppVersion14Preferences = AppVersion14Preferences()
)