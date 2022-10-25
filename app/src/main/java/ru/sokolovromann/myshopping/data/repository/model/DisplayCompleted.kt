package ru.sokolovromann.myshopping.data.repository.model

enum class DisplayCompleted {

    FIRST, LAST, HIDE;

    companion object {
        val DefaultValue: DisplayCompleted = LAST

        fun valueOfOrDefault(value: String): DisplayCompleted = try {
            valueOf(value)
        } catch (e: Exception) {
            DefaultValue
        }
    }
}