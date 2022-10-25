package ru.sokolovromann.myshopping.data.repository.model

data class ShoppingListNotification(
    val shoppingList: ShoppingList = ShoppingList(),
    val preferences: ShoppingListPreferences = ShoppingListPreferences()
) {

    fun title(): String {
        return shoppingList.name.formatFirst(preferences.firstLetterUppercase)
    }

    fun body(): String {
        var body = ""
        shoppingList.products
            .sortProducts(preferences.sort, preferences.displayCompleted)
            .forEach { body += "${it.name.formatFirst(preferences.firstLetterUppercase)}, " }

        return body.dropLast(2)
    }
}