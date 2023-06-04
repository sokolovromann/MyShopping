package ru.sokolovromann.myshopping.data.repository.model

enum class DisplayProducts {

    COLUMNS, ROW, HIDE;

    companion object {
        val DefaultValue: DisplayProducts = COLUMNS

        fun valueOfOrDefault(value: String): DisplayProducts = try {
            valueOf(value)
        } catch (e: Exception) {
            DefaultValue
        }
    }
}