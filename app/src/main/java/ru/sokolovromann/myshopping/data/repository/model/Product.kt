package ru.sokolovromann.myshopping.data.repository.model

import java.util.UUID

data class Product(
    val productUid: String = UUID.randomUUID().toString(),
    val shoppingUid: String = "",
    val created: Long = System.currentTimeMillis(),
    val lastModified: Long = created,
    val name: String = "",
    val quantity: Quantity = Quantity(),
    val price: Money = Money(),
    val discount: Discount = Discount(),
    val taxRate: TaxRate = TaxRate(),
    val completed: Boolean = false
) {

    fun calculateTotal(): Money {
        val quantityValue = if (quantity.isEmpty()) 1f else quantity.value
        val discountValue = discount.calculate(price).value
        val taxRateValue = taxRate.calculate(price).value

        val total = quantityValue * price.value - discountValue + taxRateValue
        return Money(total, price.currency)
    }
}