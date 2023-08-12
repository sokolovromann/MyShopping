package ru.sokolovromann.myshopping.data.repository.model

data class Products(
    val shoppingList: ShoppingList = ShoppingList(),
    val shoppingListsLastPosition: Int? = null,
    val appConfig: AppConfig = AppConfig()
) {

    private val preferences = appConfig.userPreferences

    fun formatName(): String {
        return shoppingList.name
    }

    fun total(): Money {
        return shoppingList.total
    }

    fun totalFormatted(): Boolean {
        return shoppingList.totalFormatted
    }

    fun getActivePinnedProducts(
        displayCompleted: DisplayCompleted? = preferences.displayCompleted
    ): List<Product> {
        val sort = if (shoppingList.sortFormatted) shoppingList.sort else Sort()
        val sorted = getPinnedAndOtherProducts().first.sortProducts(sort)
        return if (displayCompleted == null || displayCompleted == DisplayCompleted.NO_SPLIT) {
            sorted
        } else {
            sorted.splitProducts(displayCompleted)
        }
    }

    fun getOtherProducts(
        displayCompleted: DisplayCompleted? = preferences.displayCompleted
    ): List<Product> {
        val sort = if (shoppingList.sortFormatted) shoppingList.sort else Sort()
        val sorted = getPinnedAndOtherProducts().second.sortProducts(sort)
        return if (displayCompleted == null || displayCompleted == DisplayCompleted.NO_SPLIT) {
            sorted
        } else {
            sorted.splitProducts(displayCompleted)
        }
    }

    fun calculateTotal(
        displayTotal: DisplayTotal = preferences.displayTotal,
        forceCalculate: Boolean = !totalFormatted()
    ): Money {
        return if (forceCalculate) {
            var all = 0f
            var completed = 0f
            var active = 0f

            shoppingList.products.forEach { product ->
                val totalValue = product.formatTotal().getFormattedValueWithoutSeparators().toFloat()

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

            Money(total, preferences.currency)
        } else {
            total()
        }
    }

    fun calculateTotal(uids: List<String>): Money {
        var total = 0f

        shoppingList.products.forEach { product ->
            val totalValue = product.formatTotal().getFormattedValueWithoutSeparators().toFloat()
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

    fun isAutomaticSorting(): Boolean {
        return shoppingList.sortFormatted
    }

    fun isProductPinned(uid: String): Boolean {
        return getActivePinnedProducts().find { it.productUid == uid } != null
    }

    fun isEmpty(): Boolean {
        return shoppingList.products.isEmpty()
    }

    private fun getPinnedAndOtherProducts(): Pair<List<Product>, List<Product>> {
        return shoppingList.products.partition {
            if (preferences.displayCompleted == DisplayCompleted.NO_SPLIT) {
                it.pinned
            } else {
                it.pinned && !it.completed
            }
        }
    }
}