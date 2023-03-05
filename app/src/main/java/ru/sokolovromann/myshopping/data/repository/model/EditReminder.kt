package ru.sokolovromann.myshopping.data.repository.model

data class EditReminder(
    val shoppingList: ShoppingList? = null,
    val preferences: AppPreferences = AppPreferences()
)