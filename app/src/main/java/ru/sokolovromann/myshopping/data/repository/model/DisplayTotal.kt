package ru.sokolovromann.myshopping.data.repository.model

enum class DisplayTotal {

    ALL, COMPLETED, ACTIVE;

    companion object {
        val DefaultValue: DisplayTotal = ALL

        fun valueOfOrDefault(value: String): DisplayTotal = try {
            valueOf(value)
        } catch (e: Exception) {
            DefaultValue
        }
    }
}