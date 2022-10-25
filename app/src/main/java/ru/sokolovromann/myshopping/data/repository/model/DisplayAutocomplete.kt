package ru.sokolovromann.myshopping.data.repository.model

enum class DisplayAutocomplete {

    ALL, NAME, HIDE;

    companion object {
        val DefaultValue: DisplayAutocomplete = ALL

        fun valueOfOrDefault(value: String): DisplayAutocomplete = try {
            valueOf(value)
        } catch (e: Exception) {
            DefaultValue
        }
    }
}