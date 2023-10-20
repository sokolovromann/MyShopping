package ru.sokolovromann.myshopping.data.repository.model

import ru.sokolovromann.myshopping.data.model.Money
import java.text.DecimalFormat
import java.util.UUID

@Deprecated("Use /model/product")
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
    val discount: Money = Money(),
    val taxRate: Money = Money(),
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
            if (total.isEmpty()) price else total
        } else {
            val quantityValue = if (quantity.isEmpty()) 1f else quantity.value
            val total = quantityValue * price.value
            val discountValue = discount.calculateValueFromPercent(total)
            val taxRateValue = taxRate.calculateValueFromPercent(total)

            val totalWithDiscountAndTaxRate = total - discountValue + taxRateValue
            Money(totalWithDiscountAndTaxRate, price.currency, false, getDecimalFormat())
        }
    }

    fun discountToMoney(): Money {
        val discountValue = discount.calculateValueFromPercent(calculateTotalWithoutDiscountAndTaxRate())
        return Money(discountValue, price.currency)
    }

    fun taxRateToMoney(): Money {
        val taxRateValue = taxRate.calculateValueFromPercent(calculateTotalWithoutDiscountAndTaxRate())
        return Money(taxRateValue, price.currency)
    }

    private fun calculateTotalWithoutDiscountAndTaxRate(): Float {
        return quantity.value * price.value
    }

    private fun getDecimalFormat(): DecimalFormat {
        return DecimalFormat().apply {
            minimumFractionDigits = price.decimalFormat.minimumFractionDigits
            maximumFractionDigits = price.decimalFormat.maximumFractionDigits
            roundingMode = price.decimalFormat.roundingMode
        }
    }
}