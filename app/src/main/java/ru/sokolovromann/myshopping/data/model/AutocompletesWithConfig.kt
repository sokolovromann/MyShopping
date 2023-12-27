package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.data.utils.asSearchQuery
import ru.sokolovromann.myshopping.data.utils.sortedAutocompletes
import ru.sokolovromann.myshopping.data.utils.uppercaseFirst

data class AutocompletesWithConfig(
    val autocompletes: List<Autocomplete> = listOf(),
    val appConfig: AppConfig = AppConfig()
) {

    private val userPreferences = appConfig.userPreferences

    fun groupAutocompletesByName(): Map<String, List<Autocomplete>> {
        return autocompletes
            .sortedAutocompletes()
            .groupBy { it.name.asSearchQuery() }
            .mapKeys { it.key.uppercaseFirst() }
            .mapValues {
                it.value.sortedByDescending { autocomplete -> autocomplete.lastModified.millis }
            }
    }

    fun distinctByBrand(autocompletes: List<Autocomplete>): List<String> {
        return autocompletes
            .distinctBy { it.brand }
            .filterIndexed { index, autocomplete ->
                autocomplete.brand.isNotEmpty() && index < userPreferences.maxAutocompletesOthers
            }
            .map { it.brand }
    }

    fun distinctBySize(autocompletes: List<Autocomplete>): List<String> {
        return autocompletes
            .distinctBy { it.size }
            .filterIndexed { index, autocomplete ->
                autocomplete.size.isNotEmpty() && index < userPreferences.maxAutocompletesOthers
            }
            .map { it.size }
    }

    fun distinctByColor(autocompletes: List<Autocomplete>): List<String> {
        return autocompletes
            .distinctBy { it.color }
            .filterIndexed { index, autocomplete ->
                autocomplete.color.isNotEmpty() && index < userPreferences.maxAutocompletesOthers
            }
            .map { it.color }
    }

    fun distinctByManufacturer(autocompletes: List<Autocomplete>): List<String> {
        return autocompletes
            .distinctBy { it.manufacturer }
            .filterIndexed { index, autocomplete ->
                autocomplete.manufacturer.isNotEmpty() && index < userPreferences.maxAutocompletesOthers
            }
            .map { it.manufacturer }
    }

    fun distinctByQuantity(autocompletes: List<Autocomplete>): List<Quantity> {
        return autocompletes
            .distinctBy { it.quantity.getFormattedValue() }
            .filterIndexed { index, autocomplete ->
                autocomplete.quantity.isNotEmpty() && index < userPreferences.maxAutocompletesQuantities
            }
            .map { it.quantity }
    }

    fun distinctByPrice(autocompletes: List<Autocomplete>): List<Money> {
        return if (userPreferences.displayMoney) {
            autocompletes
                .distinctBy { it.price.getFormattedValue() }
                .filterIndexed { index, autocomplete ->
                    autocomplete.price.isNotEmpty() && index < userPreferences.maxAutocompletesMoneys
                }
                .map { it.price }
        } else {
            listOf()
        }
    }

    fun distinctByDiscount(autocompletes: List<Autocomplete>): List<Money> {
        return if (userPreferences.displayMoney) {
            autocompletes
                .distinctBy { it.discount.getFormattedValue() }
                .filterIndexed { index, autocomplete ->
                    autocomplete.discount.isNotEmpty() && index < userPreferences.maxAutocompletesMoneys
                }
                .map { it.discount }
        } else {
            listOf()
        }
    }

    fun distinctByTotal(autocompletes: List<Autocomplete>): List<Money> {
        return if (userPreferences.displayMoney) {
            autocompletes
                .distinctBy { it.total.getFormattedValue() }
                .filterIndexed { index, autocomplete ->
                    autocomplete.total.isNotEmpty() && index < userPreferences.maxAutocompletesMoneys
                }
                .map { it.total }
        } else {
            listOf()
        }
    }

    fun getNames(): List<String> {
        return groupAutocompletesByName().keys.toList()
    }

    fun getUidsByNames(names: List<String>): List<String> {
        val namesAsSearch = names.map { it.asSearchQuery() }
        return autocompletes
            .filter { namesAsSearch.contains(it.name.asSearchQuery()) }
            .map { it.uid }
    }

    fun isEmpty(): Boolean {
        return autocompletes.isEmpty()
    }
}