package ru.sokolovromann.myshopping.data.repository.model

data class CalculateChange(
    val shoppingList: ShoppingList? = null,
    val appConfig: AppConfig = AppConfig()
) {

    fun calculateTotal(): Money {
        if (shoppingList == null) {
            return Money()
        }
        return shoppingList.calculateTotal()
    }
}