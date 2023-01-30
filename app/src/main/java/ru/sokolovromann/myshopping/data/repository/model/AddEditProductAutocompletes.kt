package ru.sokolovromann.myshopping.data.repository.model

data class AddEditProductAutocompletes(
    val autocompletes: List<Autocomplete> = listOf(),
    val preferences: ProductPreferences = ProductPreferences()
) {

    private val defaultNamesLimit: Int = 10

    fun names(): List<String> {
        return autocompletes
            .map { it.name.formatFirst(preferences.firstLetterUppercase) }
            .distinct()
            .sorted()
            .filterIndexed { index, name ->
                name.isNotEmpty() && index < defaultNamesLimit
            }
    }
}