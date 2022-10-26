package ru.sokolovromann.myshopping.data.repository.model

data class AddEditAutocomplete(
    val autocomplete: Autocomplete = Autocomplete(),
    val preferences: AutocompletePreferences = AutocompletePreferences()
) {

    fun formatName(): String {
        return autocomplete.name.formatFirst(preferences.firstLetterUppercase)
    }
}