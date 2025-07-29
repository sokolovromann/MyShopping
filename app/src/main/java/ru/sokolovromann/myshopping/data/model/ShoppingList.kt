package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.data.utils.sortedProducts
import ru.sokolovromann.myshopping.data.utils.toSingleList
import java.math.BigDecimal

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

    fun getProductsByUids(productUids: List<String>): List<Product> {
        return products.filter { product -> productUids.find { it == product.productUid } != null }
    }

    fun calculateTotalByProductUids(productUids: List<String>): Money {
        var total = BigDecimal.ZERO
        products.forEach { product ->
            val totalValue = product.total.getFormattedValueWithoutSeparators().toBigDecimal()
            if (productUids.contains(product.productUid)) {
                total = total.plus(totalValue)
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
        var all = BigDecimal.ZERO
        var completed = BigDecimal.ZERO
        var active = BigDecimal.ZERO

        products.forEach { product ->
            val totalValue = product.total.getFormattedValueWithoutSeparators().toBigDecimal()

            all = all.plus(totalValue)
            if (product.completed) {
                completed = completed.plus(totalValue)
            } else {
                active = active.plus(totalValue)
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
        var total = BigDecimal.ZERO
        products.forEach { product ->
            val totalValue = product.getCost().getFormattedValueWithoutSeparators().toBigDecimal()
            if (productUids.contains(product.productUid)) {
                total = total.plus(totalValue)
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
        var all = BigDecimal.ZERO
        var completed = BigDecimal.ZERO
        var active = BigDecimal.ZERO

        products.forEach { product ->
            val totalValue = product.getCost()
                .getFormattedValueWithoutSeparators().toBigDecimal()

            all = all.plus(totalValue)
            if (product.completed) {
                completed = completed.plus(totalValue)
            } else {
                active = active.plus(totalValue)
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
        var discounts = BigDecimal.ZERO
        products.forEach { product ->
            val discountsValue = product.getDiscountAsMoney()
                .getFormattedValueWithoutSeparators().toBigDecimal()
            if (productUids.contains(product.productUid)) {
                discounts = discounts.plus(discountsValue)
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
        var all = BigDecimal.ZERO
        var completed = BigDecimal.ZERO
        var active = BigDecimal.ZERO

        products.forEach { product ->
            val discountsValue = product.getDiscountAsMoney()
                .getFormattedValueWithoutSeparators().toBigDecimal()

            all = all.plus(discountsValue)
            if (product.completed) {
                completed = completed.plus(discountsValue)
            } else {
                active = active.plus(discountsValue)
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
        var taxRates = BigDecimal.ZERO
        products.forEach { product ->
            val taxRatesValue = product.getTaxRateAsMoney(userTaxRate)
                .getFormattedValueWithoutSeparators().toBigDecimal()
            if (productUids.contains(product.productUid)) {
                taxRates = taxRates.plus(taxRatesValue)
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
        var all = BigDecimal.ZERO
        var completed = BigDecimal.ZERO
        var active = BigDecimal.ZERO

        products.forEach { product ->
            val taxRatesValue = product.getTaxRateAsMoney(userTaxRate)
                .getFormattedValueWithoutSeparators().toBigDecimal()

            all = all.plus(taxRatesValue)
            if (product.completed) {
                completed = completed.plus(taxRatesValue)
            } else {
                active = active.plus(taxRatesValue)
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