package ru.sokolovromann.myshopping.data.repository.model

enum class SortBy {

    CREATED, LAST_MODIFIED, NAME, TOTAL;

    companion object {
        val DefaultValue: SortBy = CREATED

        fun valueOfOrDefault(value: String): SortBy = try {
            valueOf(value)
        } catch (e: Exception) {
            DefaultValue
        }
    }
}