package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.data.repository.model.AppPreferences
import ru.sokolovromann.myshopping.data.repository.model.Autocomplete
import ru.sokolovromann.myshopping.data.repository.model.Autocompletes
import ru.sokolovromann.myshopping.data.repository.model.formatFirst
import ru.sokolovromann.myshopping.ui.compose.state.AutocompleteItems
import ru.sokolovromann.myshopping.ui.compose.state.UiText

fun Autocompletes.getAutocompleteItems(): Map<UiText, AutocompleteItems> {
    val items: MutableMap<UiText, AutocompleteItems> = mutableMapOf()
    formatAutocompletes().groupBy { it.name.lowercase() }.forEach {
        val name: UiText = UiText.FromString(it.key.formatFirst(true))
        val autocompletes = toAutocompleteItems(it.value, preferences)
        items[name] = autocompletes
    }
    return items
}

private fun toAutocompleteItems(
    autocompletes: List<Autocomplete>,
    preferences: AppPreferences
): AutocompleteItems {
    val quantitiesLimit = 5
    val pricesLimit = 3
    val discountsLimit = 3
    val totalsLimit = 3

    val quantitiesList: List<UiText> = autocompletes
        .sortedByDescending { it.lastModified }
        .distinctBy { it.quantity.formatValue() }
        .filterIndexed { index, autocomplete ->
            autocomplete.quantity.isNotEmpty() && index < quantitiesLimit
        }
        .map { UiText.FromString(it.quantity.toString()) }

    val pricesList: List<UiText> = if (preferences.displayMoney) {
        autocompletes
            .sortedByDescending { it.lastModified }
            .distinctBy { it.price.formatValue() }
            .filterIndexed { index, autocomplete ->
                autocomplete.price.isNotEmpty() && index < pricesLimit
            }
            .map { UiText.FromString(it.price.toString()) }
    } else {
        listOf()
    }

    val discountsList: List<UiText> = if (preferences.displayMoney) {
        autocompletes
            .sortedByDescending { it.lastModified }
            .distinctBy { it.discount.formatValue() }
            .filterIndexed { index, autocomplete ->
                autocomplete.discount.isNotEmpty() && index < discountsLimit
            }
            .map { UiText.FromString(it.discount.toString()) }
    } else {
        listOf()
    }

    val totalsList: List<UiText> = if (preferences.displayMoney) {
        autocompletes
            .sortedByDescending { it.lastModified }
            .distinctBy { it.total.formatValue() }
            .filterIndexed { index, autocomplete ->
                autocomplete.total.isNotEmpty() && index < totalsLimit
            }
            .map { UiText.FromString(it.total.toString()) }
    } else {
        listOf()
    }

    return AutocompleteItems(
        quantitiesList = quantitiesList,
        pricesList = pricesList,
        discountsList = discountsList,
        totalsList = totalsList
    )
}