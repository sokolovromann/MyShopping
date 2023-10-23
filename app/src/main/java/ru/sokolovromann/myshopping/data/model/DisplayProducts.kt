package ru.sokolovromann.myshopping.data.model

enum class DisplayProducts(private val displayProductsName: String) {

    VERTICAL(displayProductsName = "COLUMNS"),
    HORIZONTAL(displayProductsName = "ROW"),
    HIDE(displayProductsName = "HIDE"),
    HIDE_IF_HAS_TITLE(displayProductsName = "HIDE_IF_HAS_TITLE");

    companion object {
        val DefaultValue: DisplayProducts = HIDE_IF_HAS_TITLE

        fun valueOfOrDefault(name: String?): DisplayProducts = try {
            val value = name ?: throw NullPointerException()
            when (value) {
                "COLUMNS" -> VERTICAL
                "ROW" -> HORIZONTAL
                else -> valueOf(value)
            }
        } catch (e: Exception) {
            DefaultValue
        }
    }

    override fun toString(): String {
        return displayProductsName
    }
}