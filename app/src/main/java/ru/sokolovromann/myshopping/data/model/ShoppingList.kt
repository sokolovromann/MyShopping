package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.data.utils.sortedProducts
import ru.sokolovromann.myshopping.data.utils.toProductsList

data class ShoppingList(
    val shopping: Shopping = Shopping(),
    val products: List<Product> = listOf()
) {

    fun getSortedProducts(displayCompleted: DisplayCompleted): List<Product> {
        return getPinnedOtherSortedProducts(displayCompleted).toProductsList()
    }

    fun getPinnedOtherSortedProducts(displayCompleted: DisplayCompleted): Pair<List<Product>, List<Product>> {
        val sort = if (shopping.sortFormatted) shopping.sort else Sort()
        return products
            .sortedProducts(sort, displayCompleted)
            .partition { it.pinned && !it.completed }
    }

    fun getProductUids(): List<String> {
        return products.map { it.productUid }
    }

    fun calculateTotalByProductUids(productUids: List<String>): Money {
        var total = 0f
        products.forEach { product ->
            val totalValue = product.total.getFormattedValueWithoutSeparators().toFloat()
            if (productUids.contains(product.productUid)) {
                total += totalValue
            }
        }

        return Money(
            value = total,
            currency = shopping.total.currency,
            asPercent = false,
            decimalFormat = shopping.total.decimalFormat
        )
    }

    fun calculateTotalByDisplayTotal(displayTotal: DisplayTotal): Money {
        var all = 0f
        var completed = 0f
        var active = 0f

        products.forEach { product ->
            val totalValue = product.total.getFormattedValueWithoutSeparators().toFloat()

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

        return Money(
            value = total,
            currency = shopping.total.currency,
            asPercent = false,
            decimalFormat = shopping.total.decimalFormat
        )
    }

    fun isCompleted(): Boolean {
        return if (products.isEmpty()) {
            false
        } else {
            products.find { !it.completed } == null
        }
    }

    fun isActive(): Boolean {
        return !isCompleted()
    }

    fun isShoppingEmpty(): Boolean {
        return shopping.id == IdDefaults.NO_ID
    }

    fun isProductsEmpty(): Boolean {
        return products.isEmpty()
    }
}