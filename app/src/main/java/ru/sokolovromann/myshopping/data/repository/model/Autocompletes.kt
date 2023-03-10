package ru.sokolovromann.myshopping.data.repository.model

data class Autocompletes(
    val autocompletes: List<Autocomplete> = listOf(),
    val preferences: AppPreferences = AppPreferences()
) {

    private val defaultNamesLimit: Int = 10
    private val defaultQuantitiesLimit: Int = 5
    private val defaultPricesLimit: Int = 3
    private val defaultDiscountsLimit: Int = 3
    private val defaultTotalsLimit: Int = 3

    fun formatAutocompletes(): List<Autocomplete> {
        return autocompletes.sortAutocompletes()
    }

    fun names(): List<Autocomplete> {
        return autocompletes
            .sortAutocompletes()
            .distinctBy { it.name.lowercase() }
            .filterIndexed { index, autocomplete ->
                autocomplete.name.isNotEmpty() && index < defaultNamesLimit
            }
    }

    fun quantities(): List<Quantity> {
        return autocompletes
            .sortedByDescending { it.lastModified }
            .map { it.quantity }
            .distinctBy { it.value }
            .filterIndexed { index, quantity ->
                quantity.isNotEmpty() && index < defaultQuantitiesLimit
            }
    }

    fun quantitySymbols(): List<Quantity> {
        return autocompletes
            .sortedByDescending { it.lastModified }
            .map { it.quantity }
            .distinctBy { it.symbol }
            .filterIndexed { index, quantity ->
                quantity.value > 0 && quantity.symbol.isNotEmpty() && index < defaultQuantitiesLimit
            }
    }

    fun prices(): List<Money> {
        return autocompletes
            .sortedByDescending { it.lastModified }
            .map { it.price }
            .distinctBy { it.value }
            .filterIndexed { index, price ->
                price.isNotEmpty() && index < defaultPricesLimit
            }
    }

    fun discounts(): List<Discount> {
        return autocompletes
            .sortedByDescending { it.lastModified }
            .map { it.discount }
            .distinctBy { it.value }
            .filterIndexed { index, discount ->
                discount.isNotEmpty() && index < defaultDiscountsLimit
            }
    }

    fun totals(): List<Money> {
        return autocompletes
            .sortedByDescending { it.lastModified }
            .map { it.total }
            .distinctBy { it.value }
            .filterIndexed { index, total ->
                total.isNotEmpty() && index < defaultTotalsLimit
            }
    }
}