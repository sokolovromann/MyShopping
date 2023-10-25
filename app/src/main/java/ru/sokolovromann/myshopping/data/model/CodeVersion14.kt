package ru.sokolovromann.myshopping.data.model

data class CodeVersion14(
    val shoppingLists: List<ShoppingList> = listOf(),
    val autocompletes: List<Autocomplete> = listOf(),
    val preferences: CodeVersion14Preferences = CodeVersion14Preferences()
)