package ru.sokolovromann.myshopping.data.repository.model

enum class FontSize {

    SMALL, MEDIUM, LARGE, HUGE, HUGE_2, HUGE_3;

    companion object {
        val DefaultValue: FontSize = MEDIUM

        fun valueOfOrDefault(name: String?): FontSize = try {
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