package ru.sokolovromann.myshopping.data.model

enum class SwipeShopping {

    DISABLED, ARCHIVE, DELETE, COMPLETE;

    companion object {
        val DefaultValue = DISABLED

        fun valueOfOrDefault(name: String?): SwipeShopping = try {
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