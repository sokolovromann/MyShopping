package ru.sokolovromann.myshopping.data.model

enum class SortBy {

    POSITION, CREATED, LAST_MODIFIED, NAME, TOTAL;

    companion object {
        val DefaultValue: SortBy = POSITION

        fun valueOfOrDefault(value: String): SortBy = try {
            valueOf(value)
        } catch (e: Exception) {
            DefaultValue
        }
    }

    override fun toString(): String {
        return name
    }
}