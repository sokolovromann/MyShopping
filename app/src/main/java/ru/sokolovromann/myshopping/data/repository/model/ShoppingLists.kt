package ru.sokolovromann.myshopping.data.repository.model

data class ShoppingLists(
    val shoppingLists: List<ShoppingList> = listOf(),
    val shoppingListsLastPosition: Int? = null,
    val preferences: AppPreferences = AppPreferences()
) {

    fun formatShoppingLists(
        displayCompleted: DisplayCompleted? = preferences.displayCompletedPurchases
    ): List<ShoppingList> {
        val sorted = shoppingLists
            .map { it.copy(products = formatProducts(it.products, displayCompleted, it.sort, it.sortFormatted)) }
            .sortShoppingLists()

        return if (displayCompleted == null) {
            sorted
        } else {
            sorted.splitShoppingLists(displayCompleted)
        }
    }

    fun calculateTotal(): Money {
        var total = 0f
        shoppingLists.forEach {
            total += if (it.totalFormatted && preferences.displayPurchasesTotal == DisplayTotal.ALL) {
                it.calculateTotal(false).value
            } else {
                it.calculateTotal(true).value
            }
        }
        return Money(total, preferences.currency)
    }

    fun calculateTotal(uids: List<String>): Money {
        var total = 0f
        shoppingLists.forEach {
            if (uids.contains(it.uid)) {
                total += if (it.totalFormatted && preferences.displayPurchasesTotal == DisplayTotal.ALL) {
                    it.calculateTotal(false).value
                } else {
                    it.calculateTotal(true).value
                }
            }
        }

        return Money(total, preferences.currency)
    }

    fun hasHiddenShoppingLists(): Boolean {
        return shoppingLists.splitShoppingLists(DisplayCompleted.FIRST).first().completed
    }

    private fun formatProducts(
        product: List<Product>,
        displayCompleted: DisplayCompleted?,
        shoppingSort: Sort,
        shoppingSortFormatted: Boolean
    ): List<Product> {
        val sort = if (shoppingSortFormatted) {
            shoppingSort
        } else {
            Sort()
        }
        val sorted = product.sortProducts(sort)

        return if (displayCompleted == null) {
            sorted
        } else {
            sorted.splitProducts(displayCompleted)
        }
    }
}