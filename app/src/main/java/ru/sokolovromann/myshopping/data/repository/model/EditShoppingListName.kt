package ru.sokolovromann.myshopping.data.repository.model

data class EditShoppingListName(
    val shoppingList: ShoppingList? = null,
    val preferences: AppPreferences = AppPreferences(),
    val appConfig: AppConfig = AppConfig()
) {

    fun name(): String {
        return shoppingList?.name ?: ""
    }
}