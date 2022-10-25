package ru.sokolovromann.myshopping.data.repository.model

enum class FontSize {

    TINY, SMALL, MEDIUM, LARGE, HUGE;

    companion object {
        val DefaultValue: FontSize = MEDIUM

        fun valueOfOrDefault(value: String): FontSize = try {
            valueOf(value)
        } catch (e: Exception) {
            DefaultValue
        }
    }
}