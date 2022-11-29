package ru.sokolovromann.myshopping.data.repository.model

data class CalculateChange(
    val shoppingList: ShoppingList? = ShoppingList(),
    val preferences: ProductPreferences = ProductPreferences()
) {

    fun calculateTotal(): Money {
        if (shoppingList == null) {
            return Money()
        }
        return shoppingList.calculateTotal()
    }
}