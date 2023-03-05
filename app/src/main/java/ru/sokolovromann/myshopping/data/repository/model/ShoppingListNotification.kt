package ru.sokolovromann.myshopping.data.repository.model

data class ShoppingListNotification(
    val shoppingList: ShoppingList = ShoppingList(),
    val preferences: AppPreferences = AppPreferences()
) {

    fun id(): Int {
        return shoppingList.id
    }

    fun title(): String {
        return shoppingList.name
    }

    fun body(): String {
        var body = ""
        shoppingList.products
            .sortProducts()
            .splitProducts(preferences.displayCompletedPurchases)
            .forEach { body += "${it.name}, " }

        return body.dropLast(2)
    }
}