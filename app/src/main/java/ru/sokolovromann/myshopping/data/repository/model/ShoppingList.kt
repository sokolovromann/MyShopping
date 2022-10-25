package ru.sokolovromann.myshopping.data.repository.model

import java.util.UUID

data class ShoppingList(
    val uid: String = UUID.randomUUID().toString(),
    val created: Long = System.currentTimeMillis(),
    val lastModified: Long = created,
    val name: String = "",
    val reminder: Long? = null,
    val archived: Boolean = false,
    val deleted: Boolean = false,
    val products: List<Product> = listOf(),
    val currency: Currency = Currency(),
    val displayTotal: DisplayTotal = DisplayTotal.DefaultValue
) {

    fun calculateTotal(): Money {
        var all = 0f
        var completed = 0f
        var active = 0f

        products.forEach { product ->
            val totalValue = product.calculateTotal().value

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

        return Money(total, currency)
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
}