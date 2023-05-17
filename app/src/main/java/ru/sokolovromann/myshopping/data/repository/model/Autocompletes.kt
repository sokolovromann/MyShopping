package ru.sokolovromann.myshopping.data.repository.model

data class Autocompletes(
    val autocompletes: List<Autocomplete> = listOf(),
    val preferences: AppPreferences = AppPreferences()
) {

    private val defaultNamesLimit: Int = 10
    private val defaultQuantitiesLimit: Int = 5
    private val defaultMoneyLimit: Int = 3

    fun formatAutocompletes(): List<Autocomplete> {
        return autocompletes.sortAutocompletes()
    }

    fun names(search: String = ""): List<Autocomplete> {
        val endIndex = search.length - 1
        val partition = autocompletes.partition {
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

    fun quantities(): List<Quantity> {
        return autocompletes
            .sortedByDescending { it.lastModified }
            .map { it.quantity }
            .distinctBy { it.formatValue() }
            .filterIndexed { index, quantity ->
                quantity.isNotEmpty() && index <= defaultQuantitiesLimit
            }
    }

    fun quantitySymbols(): List<Quantity> {
        return autocompletes
            .sortedByDescending { it.lastModified }
            .map { it.quantity }
            .distinctBy { it.symbol }
            .filterIndexed { index, quantity ->
                quantity.value > 0 && quantity.symbol.isNotEmpty() && index <= defaultQuantitiesLimit
            }
    }

    fun prices(): List<Money> {
        return autocompletes
            .sortedByDescending { it.lastModified }
            .map { it.price }
            .distinctBy { it.formatValue() }
            .filterIndexed { index, price ->
                price.isNotEmpty() && index <= defaultMoneyLimit
            }
    }

    fun discounts(): List<Discount> {
        return autocompletes
            .sortedByDescending { it.lastModified }
            .map { it.discount }
            .distinctBy { it.formatValue() }
            .filterIndexed { index, discount ->
                discount.isNotEmpty() && index <= defaultMoneyLimit
            }
    }

    fun totals(): List<Money> {
        return autocompletes
            .sortedByDescending { it.lastModified }
            .map { it.total }
            .distinctBy { it.formatValue() }
            .filterIndexed { index, total ->
                total.isNotEmpty() && index <= defaultMoneyLimit
            }
    }
}