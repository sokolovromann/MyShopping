package ru.sokolovromann.myshopping.data.repository.model

data class AddEditProductAutocomplete(
    val autocompletes: List<Autocomplete> = listOf(),
    val preferences: ProductPreferences = ProductPreferences()
) {

    private val defaultNamesLimit: Int = 10
    private val defaultQuantitiesLimit: Int = 5
    private val defaultPricesLimit: Int = 3
    private val defaultDiscountsLimit: Int = 3
    private val defaultTotalsLimit: Int = 3

    private fun filterAutocompletesByNameOrNot(name: String): List<Autocomplete> {
        return if (name.isEmpty()) {
            autocompletes
        } else {
            if (autocompletes.find { it.name == name } == null) {
                autocompletes.filter { it.name.contains(name, true) }
            } else {
                autocompletes.filter { it.name == name }
            }
        }
    }

    fun names(): List<String> {
        return autocompletes
            .map { it.name.formatFirst(preferences.firstLetterUppercase) }
            .distinct()
            .sorted()
            .filterIndexed { index, name ->
                name.isNotEmpty() && index < defaultNamesLimit
            }
    }

    fun quantities(name: String = ""): List<Quantity> {
        return filterAutocompletesByNameOrNot(name)
            .sortedByDescending { it.lastModified }
            .map { it.quantity }
            .distinctBy { it.value }
            .filterIndexed { index, quantity ->
                quantity.isNotEmpty() && index < defaultQuantitiesLimit
            }
    }

    fun quantitySymbols(name: String = ""): List<Quantity> {
        return filterAutocompletesByNameOrNot(name)
            .sortedByDescending { it.lastModified }
            .map { it.quantity }
            .distinctBy { it.symbol }
            .filterIndexed { index, quantity ->
                quantity.isNotEmpty() && index < defaultQuantitiesLimit
            }
    }

    fun prices(name: String = ""): List<Money> {
        return filterAutocompletesByNameOrNot(name)
            .sortedByDescending { it.lastModified }
            .map { it.price }
            .distinctBy { it.value }
            .filterIndexed { index, price ->
                price.isNotEmpty() && index < defaultPricesLimit
            }
    }

    fun discounts(name: String = ""): List<Discount> {
        return filterAutocompletesByNameOrNot(name)
            .sortedByDescending { it.lastModified }
            .map { it.discount }
            .distinctBy { it.value }
            .filterIndexed { index, discount ->
                discount.isNotEmpty() && index < defaultDiscountsLimit
            }
    }

    fun totals(name: String = ""): List<Money> {
        return filterAutocompletesByNameOrNot(name)
            .sortedByDescending { it.lastModified }
            .map { it.calculateTotal() }
            .distinctBy { it.value }
            .filterIndexed { index, total ->
                total.isNotEmpty() && index < defaultTotalsLimit
            }
    }
}