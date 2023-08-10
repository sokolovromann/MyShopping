package ru.sokolovromann.myshopping.data.repository.model

enum class LockProductElement {

    QUANTITY, PRICE, TOTAL;

    companion object {
        val DefaultValue: LockProductElement = TOTAL

        fun valueOfOrDefault(name: String?): LockProductElement = try {
            val value = name ?: throw NullPointerException()
            valueOf(value)
        } catch (e: Exception) {
            DefaultValue
        }
    }

    override fun toString(): String {
        return name
    }
}