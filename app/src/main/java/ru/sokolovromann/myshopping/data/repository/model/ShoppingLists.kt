package ru.sokolovromann.myshopping.data.repository.model

data class ShoppingLists(
    val shoppingLists: List<ShoppingList> = listOf(),
    val shoppingListsLastPosition: Int? = 0,
    val preferences: ShoppingListPreferences = ShoppingListPreferences()
) {

    fun sortShoppingLists(): List<ShoppingList> {
        return shoppingLists
            .map {
                it.copy(
                    name = it.name.formatFirst(preferences.firstLetterUppercase),
                    products = sortProduct(it.products)
                )
            }
            .sortShoppingLists(preferences.sort, preferences.displayCompleted)
    }

    fun calculateTotal(): Money {
        var total = 0f
        shoppingLists.forEach {
            total += it.calculateTotal().value
        }
        return Money(total, preferences.currency)
    }

    private fun sortProduct(product: List<Product>): List<Product> {
        return product
            .map { it.copy(name = it.name.formatFirst(preferences.firstLetterUppercase)) }
            .sortProducts(preferences.displayCompleted)
    }
}