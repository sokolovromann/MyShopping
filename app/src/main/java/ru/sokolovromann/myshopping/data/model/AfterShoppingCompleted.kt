package ru.sokolovromann.myshopping.data.model

enum class AfterShoppingCompleted {

    NOTHING,

    /** Archive list */
    ARCHIVE,

    /** Delete list */
    DELETE,

    /** Delete only products */
    DELETE_PRODUCTS,

    /** Delete list and products */
    DELETE_LIST_AND_PRODUCTS;

    companion object {
        val DefaultValue: AfterShoppingCompleted = NOTHING

        fun valueOfOrDefault(name: String?): AfterShoppingCompleted = try {
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