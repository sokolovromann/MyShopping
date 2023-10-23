package ru.sokolovromann.myshopping.data.model

enum class DisplayTotal {

    ALL, COMPLETED, ACTIVE;

    companion object {
        val DefaultValue: DisplayTotal = ALL

        fun valueOfOrDefault(name: String?): DisplayTotal = try {
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