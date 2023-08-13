package ru.sokolovromann.myshopping.data.repository.model

data class Autocompletes(
    val autocompletes: List<Autocomplete> = listOf(),
    val appConfig: AppConfig = AppConfig()
) {

    private val defaultNamesLimit: Int = 10
    private val defaultQuantitiesLimit: Int = 5
    private val defaultMoneyLimit: Int = 3
    private val defaultOtherFieldsLimit: Int = 3

    private val preferences = appConfig.userPreferences

    fun formatAutocompletes(): List<Autocomplete> {
        return autocompletes.sortAutocompletes()
    }

    fun getNames(
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

    fun getBrands(displayDefault: Boolean = preferences.displayDefaultAutocompletes): List<String> {
        return filteredAutocompletes(displayDefault)
            .sortedByDescending { it.lastModified }
            .map { it.brand }
            .distinct()
            .filterIndexed { index, brand ->
                brand.isNotEmpty() && index <= defaultOtherFieldsLimit
            }
    }

    fun getSizes(displayDefault: Boolean = preferences.displayDefaultAutocompletes): List<String> {
        return filteredAutocompletes(displayDefault)
            .sortedByDescending { it.lastModified }
            .map { it.size }
            .distinct()
            .filterIndexed { index, size ->
                size.isNotEmpty() && index <= defaultOtherFieldsLimit
            }
    }

    fun getColors(displayDefault: Boolean = preferences.displayDefaultAutocompletes): List<String> {
        return filteredAutocompletes(displayDefault)
            .sortedByDescending { it.lastModified }
            .map { it.color }
            .distinct()
            .filterIndexed { index, color ->
                color.isNotEmpty() && index <= defaultOtherFieldsLimit
            }
    }

    fun getManufacturers(displayDefault: Boolean = preferences.displayDefaultAutocompletes): List<String> {
        return filteredAutocompletes(displayDefault)
            .sortedByDescending { it.lastModified }
            .map { it.manufacturer }
            .distinct()
            .filterIndexed { index, manufacturer ->
                manufacturer.isNotEmpty() && index <= defaultOtherFieldsLimit
            }
    }

    fun getQuantities(displayDefault: Boolean = preferences.displayDefaultAutocompletes): List<Quantity> {
        return filteredAutocompletes(displayDefault)
            .sortedByDescending { it.lastModified }
            .map { it.quantity }
            .distinctBy { it.formatValue() }
            .filterIndexed { index, quantity ->
                quantity.isNotEmpty() && index <= defaultQuantitiesLimit
            }
    }

    fun getQuantitySymbols(displayDefault: Boolean = preferences.displayDefaultAutocompletes): List<Quantity> {
        return filteredAutocompletes(displayDefault)
            .sortedByDescending { it.lastModified }
            .map { it.quantity }
            .distinctBy { it.symbol }
            .filterIndexed { index, quantity ->
                quantity.value > 0 && quantity.symbol.isNotEmpty() && index <= defaultQuantitiesLimit
            }
    }

    fun getPrices(displayDefault: Boolean = preferences.displayDefaultAutocompletes): List<Money> {
        return filteredAutocompletes(displayDefault)
            .sortedByDescending { it.lastModified }
            .map { it.price }
            .distinctBy { it.getFormattedValue() }
            .filterIndexed { index, price ->
                price.isNotEmpty() && index <= defaultMoneyLimit
            }
    }

    fun getDiscounts(displayDefault: Boolean = preferences.displayDefaultAutocompletes): List<Money> {
        return filteredAutocompletes(displayDefault)
            .sortedByDescending { it.lastModified }
            .map { it.discount }
            .distinctBy { it.getFormattedValue() }
            .filterIndexed { index, discount ->
                discount.isNotEmpty() && index <= defaultMoneyLimit
            }
    }

    fun getTotals(displayDefault: Boolean = preferences.displayDefaultAutocompletes): List<Money> {
        return filteredAutocompletes(displayDefault)
            .sortedByDescending { it.lastModified }
            .map { it.total }
            .distinctBy { it.getFormattedValue() }
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