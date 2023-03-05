package ru.sokolovromann.myshopping.data.repository.model

data class ShoppingLists(
    val shoppingLists: List<ShoppingList> = listOf(),
    val shoppingListsLastPosition: Int? = null,
    val preferences: AppPreferences = AppPreferences()
) {

    fun formatShoppingLists(): List<ShoppingList> {
        return shoppingLists
            .map { it.copy(products = formatProducts(it.products)) }
            .sortShoppingLists()
            .splitShoppingLists(preferences.displayCompletedPurchases)
    }

    fun calculateTotal(): Money {
        var total = 0f
        shoppingLists.forEach {
            total += it.calculateTotal().value
        }
        return Money(total, preferences.currency)
    }

    fun hasHiddenShoppingLists(): Boolean {
        return shoppingLists.splitShoppingLists(DisplayCompleted.FIRST).first().completed
    }

    private fun formatProducts(product: List<Product>): List<Product> {
        return product
            .sortProducts()
            .splitProducts(preferences.displayCompletedPurchases)
    }
}