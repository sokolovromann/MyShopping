package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.data.repository.model.AppConfig

data class ShoppingListsWithConfig(
    val shoppingLists: List<ShoppingList> = listOf(),
    val appConfig: AppConfig = AppConfig()
) {

    fun isEmpty(): Boolean {
        return shoppingLists.isEmpty()
    }
}