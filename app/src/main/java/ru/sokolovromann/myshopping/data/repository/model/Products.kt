package ru.sokolovromann.myshopping.data.repository.model

data class Products(
    val shoppingList: ShoppingList = ShoppingList(),
    val preferences: AppPreferences = AppPreferences()
) {

    fun formatName(): String {
        return shoppingList.name
    }

    fun formatProducts(
        displayCompleted: DisplayCompleted? = preferences.displayCompletedPurchases
    ): List<Product> {
        val sorted = shoppingList.products.sortProducts()
        return if (displayCompleted == null) {
            sorted
        } else {
            sorted.splitProducts(displayCompleted)
        }
    }

    fun calculateTotal(displayTotal: DisplayTotal = preferences.displayPurchasesTotal): Money {
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

        val total = when (displayTotal) {
            DisplayTotal.ALL -> all
            DisplayTotal.COMPLETED -> completed
            DisplayTotal.ACTIVE -> active
        }

        return Money(total, preferences.currency)
    }

    fun calculateTotal(uids: List<String>): Money {
        var total = 0f

        shoppingList.products.forEach { product ->
            val totalValue = product.formatTotal().valueToString().toFloat()
            if (uids.contains(product.productUid)) {
                total += totalValue
            }
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