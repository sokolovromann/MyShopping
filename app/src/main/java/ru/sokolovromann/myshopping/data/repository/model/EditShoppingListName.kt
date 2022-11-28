package ru.sokolovromann.myshopping.data.repository.model

data class EditShoppingListName(
    val shoppingList: ShoppingList? = ShoppingList(),
    val preferences: ProductPreferences = ProductPreferences()
) {

    fun formatName(): String {
        if (shoppingList == null) {
            return ""
        }
        return shoppingList.name.formatFirst(preferences.firstLetterUppercase)
    }
}