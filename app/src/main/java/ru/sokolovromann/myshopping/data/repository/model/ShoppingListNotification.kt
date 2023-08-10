package ru.sokolovromann.myshopping.data.repository.model

data class ShoppingListNotification(
    val shoppingList: ShoppingList = ShoppingList(),
    val preferences: AppPreferences = AppPreferences(),
    val appConfig: AppConfig = AppConfig()
) {

    fun id(): Int {
        return shoppingList.id
    }

    fun title(): String {
        return shoppingList.name
    }

    fun body(): String {
        var body = ""
        val sorted = shoppingList.products.sortProducts()
        val products = if (preferences.displayCompletedPurchases == DisplayCompleted.NO_SPLIT) {
            sorted
        } else {
            sorted.splitProducts(preferences.displayCompletedPurchases)
        }

        products.forEach { body += "${it.name}, " }
        return body.dropLast(2)
    }
}