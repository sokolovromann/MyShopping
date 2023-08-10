package ru.sokolovromann.myshopping.data.repository.model

enum class DisplayCompleted {

    FIRST, LAST, HIDE, NO_SPLIT;

    companion object {
        val DefaultValue: DisplayCompleted = LAST

        fun valueOfOrDefault(name: String?): DisplayCompleted = try {
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