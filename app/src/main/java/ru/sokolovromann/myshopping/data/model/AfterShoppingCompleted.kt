package ru.sokolovromann.myshopping.data.model

enum class AfterShoppingCompleted {

    NOTHING, ARCHIVE, DELETE;

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