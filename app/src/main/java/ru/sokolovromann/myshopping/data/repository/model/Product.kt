package ru.sokolovromann.myshopping.data.repository.model

import java.util.UUID

data class Product(
    val id: Int = 0,
    val position: Int = 0,
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
        val total = quantityValue * price.value
        val discountValue = discount.calculate(total)
        val taxRateValue = taxRate.calculate(total)

        val totalWithDiscountAndTaxRate = total - discountValue + taxRateValue
        return Money(totalWithDiscountAndTaxRate, price.currency)
    }
}