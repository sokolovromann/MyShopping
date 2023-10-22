package ru.sokolovromann.myshopping.data.model

enum class FontSize(private val fontSizeName: String) {

    SMALL(fontSizeName = "SMALL"),
    MEDIUM(fontSizeName = "MEDIUM"),
    LARGE(fontSizeName = "LARGE"),
    VERY_LARGE(fontSizeName = "HUGE"),
    HUGE(fontSizeName = "HUGE_2"),
    VERY_HUGE(fontSizeName = "HUGE_3");

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
        return fontSizeName
    }
}