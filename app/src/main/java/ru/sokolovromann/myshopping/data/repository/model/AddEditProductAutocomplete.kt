package ru.sokolovromann.myshopping.data.repository.model

data class AddEditProductAutocomplete(
    val autocompletes: List<Autocomplete> = listOf(),
    val preferences: ProductPreferences = ProductPreferences()
) {

    private val defaultNamesLimit: Int = 10

    fun names(): List<String> {
        return autocompletes
            .map { it.name.formatFirst(preferences.firstLetterUppercase) }
            .distinct()
            .sorted()
            .filterIndexed { index, _ -> index < defaultNamesLimit }
    }

    fun quantities(): List<Quantity> {
        return autocompletes
            .map { it.quantity }
            .distinctBy { it.value }
            .filter { it.isNotEmpty() }
            .sortedBy { it.value }
    }

    fun prices(): List<Money> {
        return autocompletes
            .map { it.price }
            .distinctBy { it.value }
            .filter { it.isNotEmpty() }
            .sortedBy { it.value }
    }

    fun discounts(): List<Discount> {
        return autocompletes
            .map { it.discount }
            .distinctBy { it.value }
            .filter { it.isNotEmpty() }
            .sortedBy { it.value }
    }

    fun totals(): List<Money> {
        return autocompletes
            .map { it.calculateTotal() }
            .distinctBy { it.value }
            .filter { it.isNotEmpty() }
            .sortedBy { it.value }
    }
}