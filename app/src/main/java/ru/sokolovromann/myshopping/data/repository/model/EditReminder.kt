package ru.sokolovromann.myshopping.data.repository.model

data class EditReminder(
    val shoppingList: ShoppingList? = ShoppingList(),
    val preferences: ProductPreferences = ProductPreferences()
)