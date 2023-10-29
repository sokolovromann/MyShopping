package ru.sokolovromann.myshopping.data.model

data class ShoppingList(
    val shopping: Shopping = Shopping(),
    val products: List<Product> = listOf()
) {

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
}