package ru.sokolovromann.myshopping.data.model

data class ShoppingListsWithConfig(
    val shoppingLists: List<ShoppingList> = listOf(),
    val appConfig: AppConfig = AppConfig()
) {

    fun isEmpty(): Boolean {
        return shoppingLists.isEmpty()
    }
}