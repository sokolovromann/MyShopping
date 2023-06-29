package ru.sokolovromann.myshopping.data.repository.model

data class Autocompletes(
    val autocompletes: List<Autocomplete> = listOf(),
    val preferences: AppPreferences = AppPreferences()
) {

    private val defaultNamesLimit: Int = 10
    private val defaultQuantitiesLimit: Int = 5
    private val defaultMoneyLimit: Int = 3
    private val defaultOtherFieldsLimit: Int = 3

    fun formatAutocompletes(): List<Autocomplete> {
        return autocompletes.sortAutocompletes()
    }

    fun names(
        search: String = "",
        displayDefault: Boolean = preferences.displayDefaultAutocompletes
    ): List<Autocomplete> {
        val endIndex = search.length - 1
        val partition = filteredAutocompletes(displayDefault)
            .partition {
                it.name.lowercase().toCharArray(endIndex = endIndex)
                    .contentEquals(search.lowercase().toCharArray(endIndex = endIndex))
            }
        val searchAutocompletes = partition.first
            .sortAutocompletes()
            .distinctBy { it.name.lowercase() }

        val otherAutocompletes = partition.second
            .sortAutocompletes()
            .distinctBy { it.name.lowercase() }

        val bothAutocompletes = mutableListOf<Autocomplete>()
        return bothAutocompletes
            .apply {
                addAll(searchAutocompletes)
                addAll(otherAutocompletes)
            }
            .filterIndexed { index, autocomplete ->
                autocomplete.name.isNotEmpty() && index <= defaultNamesLimit
            }
    }

    fun brands(displayDefault: Boolean = preferences.displayDefaultAutocompletes): List<String> {
        return filteredAutocompletes(displayDefault)
            .sortedByDescending { it.lastModified }
            .map { it.brand }
            .distinct()
            .filterIndexed { index, brand ->
                brand.isNotEmpty() && index <= defaultOtherFieldsLimit
            }
    }

    fun sizes(displayDefault: Boolean = preferences.displayDefaultAutocompletes): List<String> {
        return filteredAutocompletes(displayDefault)
            .sortedByDescending { it.lastModified }
            .map { it.size }
            .distinct()
            .filterIndexed { index, size ->
                size.isNotEmpty() && index <= defaultOtherFieldsLimit
            }
    }

    fun colors(displayDefault: Boolean = preferences.displayDefaultAutocompletes): List<String> {
        return filteredAutocompletes(displayDefault)
            .sortedByDescending { it.lastModified }
            .map { it.color }
            .distinct()
            .filterIndexed { index, color ->
                color.isNotEmpty() && index <= defaultOtherFieldsLimit
            }
    }

    fun manufacturers(displayDefault: Boolean = preferences.displayDefaultAutocompletes): List<String> {
        return filteredAutocompletes(displayDefault)
            .sortedByDescending { it.lastModified }
            .map { it.manufacturer }
            .distinct()
            .filterIndexed { index, manufacturer ->
                manufacturer.isNotEmpty() && index <= defaultOtherFieldsLimit
            }
    }

    fun quantities(displayDefault: Boolean = preferences.displayDefaultAutocompletes): List<Quantity> {
        return filteredAutocompletes(displayDefault)
            .sortedByDescending { it.lastModified }
            .map { it.quantity }
            .distinctBy { it.formatValue() }
            .filterIndexed { index, quantity ->
                quantity.isNotEmpty() && index <= defaultQuantitiesLimit
            }
    }

    fun quantitySymbols(displayDefault: Boolean = preferences.displayDefaultAutocompletes): List<Quantity> {
        return filteredAutocompletes(displayDefault)
            .sortedByDescending { it.lastModified }
            .map { it.quantity }
            .distinctBy { it.symbol }
            .filterIndexed { index, quantity ->
                quantity.value > 0 && quantity.symbol.isNotEmpty() && index <= defaultQuantitiesLimit
            }
    }

    fun prices(displayDefault: Boolean = preferences.displayDefaultAutocompletes): List<Money> {
        return filteredAutocompletes(displayDefault)
            .sortedByDescending { it.lastModified }
            .map { it.price }
            .distinctBy { it.formatValue() }
            .filterIndexed { index, price ->
                price.isNotEmpty() && index <= defaultMoneyLimit
            }
    }

    fun discounts(displayDefault: Boolean = preferences.displayDefaultAutocompletes): List<Discount> {
        return filteredAutocompletes(displayDefault)
            .sortedByDescending { it.lastModified }
            .map { it.discount }
            .distinctBy { it.formatValue() }
            .filterIndexed { index, discount ->
                discount.isNotEmpty() && index <= defaultMoneyLimit
            }
    }

    fun totals(displayDefault: Boolean = preferences.displayDefaultAutocompletes): List<Money> {
        return filteredAutocompletes(displayDefault)
            .sortedByDescending { it.lastModified }
            .map { it.total }
            .distinctBy { it.formatValue() }
            .filterIndexed { index, total ->
                total.isNotEmpty() && index <= defaultMoneyLimit
            }
    }

    private fun filteredAutocompletes(displayDefault: Boolean): List<Autocomplete> {
        return if (displayDefault) {
            autocompletes
        } else {
            autocompletes.filter { it.personal }
        }
    }
}