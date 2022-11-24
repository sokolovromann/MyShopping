package ru.sokolovromann.myshopping.data.repository.model

data class AddEditAutocomplete(
    val autocomplete: Autocomplete? = Autocomplete(),
    val preferences: AutocompletePreferences = AutocompletePreferences()
) {

    fun formatName(): String {
        if (autocomplete == null) {
            return ""
        }
        return autocomplete.name.formatFirst(preferences.firstLetterUppercase)
    }
}