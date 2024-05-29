package ru.sokolovromann.myshopping.data.model

enum class ContentView {

    LIST, GRID;

    companion object {
        val DefaultValue = LIST

        fun valueOfOrDefault(name: String?): ContentView {
            return try {
                val value = name ?: throw NullPointerException()
                valueOf(value)
            } catch (e: Exception) {
                DefaultValue
            }
        }
    }

    override fun toString(): String {
        return name
    }
}