package ru.sokolovromann.myshopping.data.model

enum class AfterAddShopping {

    OPEN_PRODUCTS_SCREEN,
    OPEN_EDIT_SHOPPING_NAME_SCREEN,
    OPEN_ADD_PRODUCT_SCREEN;

    companion object {
        val DefaultValue: AfterAddShopping = OPEN_PRODUCTS_SCREEN

        fun valueOfOrDefault(name: String?): AfterAddShopping = try {
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