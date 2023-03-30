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
    val total: Money = Money(),
    val totalFormatted: Boolean = false,
    val note: String = "",
    val manufacturer: String = "",
    val brand: String = "",
    val size: String = "",
    val color: String = "",
    val provider: String = "",
    val completed: Boolean = false,
    val pinned: Boolean = false
) {

    fun formatTotal(): Money {
        return if (totalFormatted) {
            total
        } else {
            val quantityValue = if (quantity.isEmpty()) 1f else quantity.value
            val total = quantityValue * price.value
            val discountValue = discount.calculate(total)
            val taxRateValue = taxRate.calculate(total)

            val totalWithDiscountAndTaxRate = total - discountValue + taxRateValue
            Money(totalWithDiscountAndTaxRate, price.currency)
        }
    }

    fun discountToMoney(): Money {
        val discountValue = discount.calculate(calculateTotalWithoutDiscountAndTaxRate())
        return Money(discountValue, price.currency)
    }

    fun taxRateToMoney(): Money {
        val taxRateValue = taxRate.calculate(calculateTotalWithoutDiscountAndTaxRate())
        return Money(taxRateValue, price.currency)
    }

    private fun calculateTotalWithoutDiscountAndTaxRate(): Float {
        return quantity.value * price.value
    }
}