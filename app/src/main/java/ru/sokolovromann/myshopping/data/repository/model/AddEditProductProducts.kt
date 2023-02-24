package ru.sokolovromann.myshopping.data.repository.model

data class AddEditProductProducts(
    val products: List<Product> = listOf(),
    val preferences: ProductPreferences = ProductPreferences()
) {
    private val defaultQuantitiesLimit: Int = 5
    private val defaultPricesLimit: Int = 3
    private val defaultDiscountsLimit: Int = 3
    private val defaultTotalsLimit: Int = 3

    fun quantities(): List<Quantity> {
        return products
            .sortedByDescending { it.lastModified }
            .map { it.quantity }
            .distinctBy { it.value }
            .filterIndexed { index, quantity ->
                quantity.isNotEmpty() && index < defaultQuantitiesLimit
            }
    }

    fun quantitySymbols(): List<Quantity> {
        return products
            .sortedByDescending { it.lastModified }
            .map { it.quantity }
            .distinctBy { it.symbol }
            .filterIndexed { index, quantity ->
                quantity.value > 0 && quantity.symbol.isNotEmpty() && index < defaultQuantitiesLimit
            }
    }

    fun prices(): List<Money> {
        return products
            .sortedByDescending { it.lastModified }
            .map { it.price }
            .distinctBy { it.value }
            .filterIndexed { index, price ->
                price.isNotEmpty() && index < defaultPricesLimit
            }
    }

    fun discounts(): List<Discount> {
        return products
            .sortedByDescending { it.lastModified }
            .map { it.discount }
            .distinctBy { it.value }
            .filterIndexed { index, discount ->
                discount.isNotEmpty() && index < defaultDiscountsLimit
            }
    }

    fun totals(): List<Money> {
        return products
            .sortedByDescending { it.lastModified }
            .map { it.total }
            .distinctBy { it.value }
            .filterIndexed { index, total ->
                total.isNotEmpty() && index < defaultTotalsLimit
            }
    }
}