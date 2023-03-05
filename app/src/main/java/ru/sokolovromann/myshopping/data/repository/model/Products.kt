package ru.sokolovromann.myshopping.data.repository.model

data class Products(
    val shoppingList: ShoppingList = ShoppingList(),
    val preferences: AppPreferences = AppPreferences()
) {

    fun formatName(): String {
        return shoppingList.name
    }

    fun formatProducts(): List<Product> {
        return shoppingList.products
            .sortProducts()
            .splitProducts(preferences.displayCompletedPurchases)
    }

    fun calculateTotal(): Money {
        var all = 0f
        var completed = 0f
        var active = 0f

        shoppingList.products.forEach { product ->
            val totalValue = product.formatTotal().valueToString().toFloat()

            all += totalValue
            if (product.completed) {
                completed += totalValue
            } else {
                active += totalValue
            }
        }

        val total = when (preferences.displayPurchasesTotal) {
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

    fun hasHiddenProducts(): Boolean {
        return shoppingList.products.splitProducts(DisplayCompleted.FIRST).first().completed
    }
}