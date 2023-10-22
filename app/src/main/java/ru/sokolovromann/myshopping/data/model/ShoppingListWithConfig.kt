package ru.sokolovromann.myshopping.data.model

data class ShoppingListWithConfig(
    val shoppingList: ShoppingList = ShoppingList(),
    val appConfig: AppConfig = AppConfig()
) {

    fun isEmpty(): Boolean {
        return shoppingList.shopping.id == IdDefaults.NO_ID
    }
}