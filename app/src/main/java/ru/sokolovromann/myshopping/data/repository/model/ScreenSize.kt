package ru.sokolovromann.myshopping.data.repository.model

enum class ScreenSize {

    SMARTPHONE, TABLET;

    companion object {
        val DefaultValue: ScreenSize = SMARTPHONE

        fun valueOfOrDefault(value: String): ScreenSize = try {
            valueOf(value)
        } catch (e: Exception) {
            DefaultValue
        }
    }
}