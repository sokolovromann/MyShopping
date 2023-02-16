package ru.sokolovromann.myshopping.data.repository.model

data class ShoppingLists(
    val shoppingLists: List<ShoppingList> = listOf(),
    val shoppingListsLastPosition: Int? = 0,
    val preferences: ShoppingListPreferences = ShoppingListPreferences()
) {

    fun formatShoppingLists(): List<ShoppingList> {
        return shoppingLists
            .map {
                it.copy(
                    name = it.name.formatFirst(preferences.firstLetterUppercase),
                    products = formatProducts(it.products)
                )
            }
            .sortShoppingLists()
            .splitShoppingLists(preferences.displayCompleted)
    }

    fun calculateTotal(): Money {
        var total = 0f
        shoppingLists.forEach {
            total += it.calculateTotal().value
        }
        return Money(total, preferences.currency)
    }

    private fun formatProducts(product: List<Product>): List<Product> {
        return product
            .map { it.copy(name = it.name.formatFirst(preferences.firstLetterUppercase)) }
            .sortProducts()
            .splitProducts(preferences.displayCompleted)
    }
}