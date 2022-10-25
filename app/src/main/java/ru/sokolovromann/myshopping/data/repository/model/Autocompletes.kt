package ru.sokolovromann.myshopping.data.repository.model

data class Autocompletes(
    val autocompletes: List<Autocomplete> = listOf(),
    val preferences: AutocompletePreferences = AutocompletePreferences()
) {

    fun sortAutocompletes(): List<Autocomplete> {
        return autocompletes
            .map { it.copy(name = it.name.formatFirst(preferences.firstLetterUppercase)) }
            .sortAutocompletes(preferences.sort)
    }
}