package ru.sokolovromann.myshopping.data.repository.model

import java.util.UUID

data class Autocomplete(
    val id: Int = 0,
    val uid: String = UUID.randomUUID().toString(),
    val created: Long = System.currentTimeMillis(),
    val lastModified: Long = created,
    val name: String = "",
    val quantity: Quantity = Quantity(),
    val price: Money = Money(),
    val discount: Discount = Discount(),
    val taxRate: TaxRate = TaxRate()
) {

    fun calculateTotal(): Money {
        val quantityValue = if (quantity.isEmpty()) 1f else quantity.value
        val discountValue = discount.calculate(price).value
        val taxRateValue = taxRate.calculate(price).value

        val total = quantityValue * price.value - discountValue + taxRateValue
        return Money(total, price.currency)
    }
}