package ru.sokolovromann.myshopping.data.repository.model

data class EditShoppingListName(
    val shoppingList: ShoppingList? = null,
    val preferences: AppPreferences = AppPreferences()
) {

    fun name(): String {
        return shoppingList?.name ?: ""
    }
}