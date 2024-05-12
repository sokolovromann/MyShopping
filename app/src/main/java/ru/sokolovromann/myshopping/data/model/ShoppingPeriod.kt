package ru.sokolovromann.myshopping.data.model

enum class ShoppingPeriod {

    ONE_MONTH, THREE_MONTHS, SIX_MONTHS, ONE_YEAR, ALL_TIME;

    companion object {
        val DefaultValue = ONE_MONTH

        fun valueOfOrDefault(name: String?): ShoppingPeriod {
            return try {
                val value = name ?: throw NullPointerException()
                valueOf(value)
            } catch (e: Exception) {
                DefaultValue
            }
        }
    }

    override fun toString(): String {
        return name
    }
}