package ru.sokolovromann.myshopping.data.repository.model

data class ShoppingLists(
    val shoppingLists: List<ShoppingList> = listOf(),
    val shoppingListsLastPosition: Int? = null,
    val preferences: AppPreferences = AppPreferences()
) {

    fun getAllShoppingLists(
        splitByPinned: Boolean = true,
        displayCompleted: DisplayCompleted? = preferences.displayCompletedPurchases
    ): List<ShoppingList> {
        val list = if (splitByPinned) {
            getActivePinnedShoppingLists()
                .toMutableList()
                .apply { addAll(getOtherShoppingLists(displayCompleted)) }
                .toList()
        } else {
            val sorted = shoppingLists
                .map {
                    val sortedProducts = getSortedProducts(it.products, displayCompleted, it.sort, it.sortFormatted)
                    it.copy(products = sortedProducts)
                }
                .sortShoppingLists()

            if (displayCompleted == null) {
                sorted
            } else {
                sorted.splitShoppingLists(displayCompleted)
            }
        }

        return list
    }

    fun getActivePinnedShoppingLists(): List<ShoppingList> {
        return getPinnedAndOtherShoppingLists().first
            .map {
                val sortedProducts = getSortedProducts(it.products, null, it.sort, it.sortFormatted)
                it.copy(products = sortedProducts)
            }
            .sortShoppingLists()
    }

    fun getOtherShoppingLists(
        displayCompleted: DisplayCompleted? = preferences.displayCompletedPurchases)
            : List<ShoppingList> {
        val sorted = getPinnedAndOtherShoppingLists().second
            .map {
                val sortedProducts = getSortedProducts(it.products, displayCompleted, it.sort, it.sortFormatted)
                it.copy(products = sortedProducts)
            }
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

    private fun getPinnedAndOtherShoppingLists(): Pair<List<ShoppingList>, List<ShoppingList>> {
        return shoppingLists.partition { it.pinned && !it.completed }
    }

    private fun getSortedProducts(
        product: List<Product>,
        displayCompleted: DisplayCompleted?,
        shoppingSort: Sort,
        shoppingSortFormatted: Boolean
    ): List<Product> {
        val sort = if (shoppingSortFormatted) shoppingSort else Sort()

        val productsPartition = product
            .sortProducts(sort)
            .partition { it.pinned }

        val sorted = productsPartition.first.toMutableList()
            .apply { addAll(productsPartition.second) }

        return if (displayCompleted == null) {
            sorted
        } else {
            sorted.splitProducts(displayCompleted)
        }
    }
}