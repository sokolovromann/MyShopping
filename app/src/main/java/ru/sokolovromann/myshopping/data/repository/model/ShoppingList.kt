package ru.sokolovromann.myshopping.data.repository.model

import java.util.UUID

data class ShoppingList(
    val id: Int = 0,
    val position: Int = 0,
    val uid: String = UUID.randomUUID().toString(),
    val created: Long = System.currentTimeMillis(),
    val lastModified: Long = created,
    val name: String = "",
    val reminder: Long? = null,
    val total: Money = Money(),
    val totalFormatted: Boolean = false,
    val budget: Money = Money(),
    val archived: Boolean = false,
    val deleted: Boolean = false,
    val completed: Boolean = false,
    val products: List<Product> = listOf(),
    val currency: Currency = Currency(),
    val displayTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val sort: Sort = Sort(),
    val sortFormatted: Boolean = false,
    val pinned: Boolean = false
) {

    fun calculateTotal(forceCalculate: Boolean = !totalFormatted): Money {
        return if (forceCalculate) {
            var all = 0f
            var completed = 0f
            var active = 0f

            products.forEach { product ->
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

            Money(total, currency, false, this.total.decimalFormat)
        } else {
            total
        }
    }

    fun nextProductsPosition(): Int {
        val lastPosition = products.maxByOrNull { it.position }?.position
        return lastPosition?.plus(1) ?: 0
    }
}