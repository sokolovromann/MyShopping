package ru.sokolovromann.myshopping.data.repository.model

enum class FontSize {

    SMALL, MEDIUM, LARGE, HUGE, HUGE_2, HUGE_3;

    companion object {
        val DefaultValue: FontSize = MEDIUM

        fun valueOfOrDefault(value: String): FontSize = try {
            valueOf(value)
        } catch (e: Exception) {
            DefaultValue
        }
    }
}