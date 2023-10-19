package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.data.repository.model.Id

data class ShoppingListWithConfig(
    val shoppingList: ShoppingList = ShoppingList(),
    val appConfig: AppConfig = AppConfig()
) {

    fun isEmpty(): Boolean {
        return shoppingList.shopping.id == Id.NO_ID
    }
}