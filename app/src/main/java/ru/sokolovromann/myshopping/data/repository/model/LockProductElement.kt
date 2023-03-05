package ru.sokolovromann.myshopping.data.repository.model

enum class LockProductElement {

    QUANTITY, PRICE, TOTAL;

    companion object {
        val DefaultValue: LockProductElement = TOTAL

        fun valueOfOrDefault(value: String): LockProductElement = try {
            valueOf(value)
        } catch (e: Exception) {
            DefaultValue
        }
    }
}