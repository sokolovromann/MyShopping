package ru.sokolovromann.myshopping.data.repository.model

enum class ProductLock {

    QUANTITY, PRICE, TOTAL;

    companion object {
        val DefaultValue: ProductLock = TOTAL

        fun valueOfOrDefault(value: String): ProductLock = try {
            valueOf(value)
        } catch (e: Exception) {
            DefaultValue
        }
    }
}