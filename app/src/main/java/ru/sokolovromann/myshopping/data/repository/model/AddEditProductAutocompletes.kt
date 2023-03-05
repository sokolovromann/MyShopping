package ru.sokolovromann.myshopping.data.repository.model

data class AddEditProductAutocompletes(
    val autocompletes: List<Autocomplete> = listOf(),
    val preferences: AppPreferences = AppPreferences()
) {

    private val defaultNamesLimit: Int = 10

    fun formatAutocompletes(): List<Autocomplete> {
        return autocompletes
            .sortAutocompletes()
            .filterIndexed { index, autocomplete ->
                autocomplete.name.isNotEmpty() && index < defaultNamesLimit
            }
    }
}