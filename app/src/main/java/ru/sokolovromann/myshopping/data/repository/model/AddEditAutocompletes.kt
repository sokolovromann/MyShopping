package ru.sokolovromann.myshopping.data.repository.model

data class AddEditAutocompletes(
    val autocomplete: Autocomplete = Autocomplete(),
    val preferences: AutocompletePreferences = AutocompletePreferences()
) {

    fun formatName(): String {
        return autocomplete.name.formatFirst(preferences.firstLetterUppercase)
    }
}