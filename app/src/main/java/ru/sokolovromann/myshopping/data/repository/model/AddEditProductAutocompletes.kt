package ru.sokolovromann.myshopping.data.repository.model

data class AddEditProductAutocompletes(
    val autocompletes: List<Autocomplete> = listOf(),
    val preferences: ProductPreferences = ProductPreferences()
) {

    private val defaultNamesLimit: Int = 10

    fun formatAutocompletes(): List<Autocomplete> {
        return autocompletes
            .map { it.copy(name = it.name.formatFirst(preferences.firstLetterUppercase)) }
            .distinctBy { it.name }
            .sortedBy { it.name }
            .filterIndexed { index, autocomplete ->
                autocomplete.name.isNotEmpty() && index < defaultNamesLimit
            }
    }
}