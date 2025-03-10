package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.data.utils.sortedProducts
import ru.sokolovromann.myshopping.data.utils.toSingleList

data class ShoppingList(
    val shopping: Shopping = Shopping(),
    val products: List<Product> = listOf()
) {

    fun getSortedProducts(displayCompleted: DisplayCompleted): List<Product> {
        return getPinnedOtherSortedProducts(displayCompleted).toSingleList()
    }

    fun getPinnedOtherSortedProducts(displayCompleted: DisplayCompleted): Pair<List<Product>, List<Product>> {
        val sort = if (shopping.sortFormatted) shopping.sort else Sort()
        return products
            .sortedProducts(sort, displayCompleted)
            .partition { it.pinned && !it.completed }
    }

    fun getTotalWithoutDiscount(): Money {
        return shopping.getTotalWithoutDiscount()
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

    fun calculateCostByProductUids(productUids: List<String>): Money {
        var total = 0f
        products.forEach { product ->
            val totalValue = product.getCost().getFormattedValueWithoutSeparators().toFloat()
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

    fun calculateCostByDisplayTotal(displayTotal: DisplayTotal): Money {
        var all = 0f
        var completed = 0f
        var active = 0f

        products.forEach { product ->
            val totalValue = product.getCost()
                .getFormattedValueWithoutSeparators().toFloat()

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

    fun calculateDiscountsByProductUids(productUids: List<String>): Money {
        var discounts = 0f
        products.forEach { product ->
            val discountsValue = product.getDiscountAsMoney()
                .getFormattedValueWithoutSeparators().toFloat()
            if (productUids.contains(product.productUid)) {
                discounts += discountsValue
            }
        }

        return Money(
            value = discounts,
            currency = shopping.total.currency,
            asPercent = false,
            decimalFormat = shopping.total.decimalFormat
        )
    }

    fun calculateDiscountsByDisplayTotal(displayTotal: DisplayTotal): Money {
        var all = 0f
        var completed = 0f
        var active = 0f

        products.forEach { product ->
            val discountsValue = product.getDiscountAsMoney()
                .getFormattedValueWithoutSeparators().toFloat()

            all += discountsValue
            if (product.completed) {
                completed += discountsValue
            } else {
                active += discountsValue
            }
        }

        val discounts = when (displayTotal) {
            DisplayTotal.ALL -> all
            DisplayTotal.COMPLETED -> completed
            DisplayTotal.ACTIVE -> active
        }

        return Money(
            value = discounts,
            currency = shopping.total.currency,
            asPercent = false,
            decimalFormat = shopping.total.decimalFormat
        )
    }

    fun calculateTaxRatesByProductUids(productUids: List<String>, userTaxRate: Money): Money {
        var taxRates = 0f
        products.forEach { product ->
            val taxRatesValue = product.getTaxRateAsMoney(userTaxRate)
                .getFormattedValueWithoutSeparators().toFloat()
            if (productUids.contains(product.productUid)) {
                taxRates += taxRatesValue
            }
        }

        return Money(
            value = taxRates,
            currency = shopping.total.currency,
            asPercent = false,
            decimalFormat = shopping.total.decimalFormat
        )
    }

    fun calculateTaxRatesByDisplayTotal(displayTotal: DisplayTotal, userTaxRate: Money): Money {
        var all = 0f
        var completed = 0f
        var active = 0f

        products.forEach { product ->
            val taxRatesValue = product.getTaxRateAsMoney(userTaxRate)
                .getFormattedValueWithoutSeparators().toFloat()

            all += taxRatesValue
            if (product.completed) {
                completed += taxRatesValue
            } else {
                active += taxRatesValue
            }
        }

        val taxRates = when (displayTotal) {
            DisplayTotal.ALL -> all
            DisplayTotal.COMPLETED -> completed
            DisplayTotal.ACTIVE -> active
        }

        return Money(
            value = taxRates,
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

    fun isProductsNotEmpty(): Boolean {
        return products.isNotEmpty()
    }
}