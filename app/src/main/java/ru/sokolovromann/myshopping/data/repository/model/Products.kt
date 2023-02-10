package ru.sokolovromann.myshopping.data.repository.model

data class Products(
    val shoppingList: ShoppingList = ShoppingList(),
    val preferences: ProductPreferences = ProductPreferences()
) {

    fun formatName(): String {
        return shoppingList.name.formatFirst(preferences.firstLetterUppercase)
    }

    fun sortProducts(): List<Product> {
        return shoppingList.products
            .map { it.copy(name = it.name.formatFirst(preferences.firstLetterUppercase)) }
            .sortProducts(preferences.displayCompleted)
    }

    fun calculateTotal(): Money {
        var all = 0f
        var completed = 0f
        var active = 0f

        shoppingList.products.forEach { product ->
            val totalValue = product.calculateTotal().value

            all += totalValue
            if (product.completed) {
                completed += totalValue
            } else {
                active += totalValue
            }
        }

        val total = when (preferences.displayTotal) {
            DisplayTotal.ALL -> all
            DisplayTotal.COMPLETED -> completed
            DisplayTotal.ACTIVE -> active
        }

        return Money(total, preferences.currency)
    }

    fun isCompleted(): Boolean {
        return shoppingList.completed
    }

    fun isActive(): Boolean {
        return !shoppingList.completed
    }
}