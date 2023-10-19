package ru.sokolovromann.myshopping.data.repository.model

import ru.sokolovromann.myshopping.data.model.AppConfig

@Deprecated("Use ShoppingListWithConfig")
data class ShoppingListNotification(
    val shoppingList: ShoppingList = ShoppingList(),
    val appConfig: AppConfig = AppConfig()
) {

    private val preferences = appConfig.userPreferences

    fun id(): Int {
        return shoppingList.id
    }

    fun title(): String {
        return shoppingList.name
    }

    fun body(): String {
        var body = ""
        val sorted = shoppingList.products.sortProducts()
        val products = if (preferences.displayCompleted == DisplayCompleted.NO_SPLIT) {
            sorted
        } else {
            sorted.splitProducts(preferences.displayCompleted)
        }

        products.forEach { body += "${it.name}, " }
        return body.dropLast(2)
    }
}