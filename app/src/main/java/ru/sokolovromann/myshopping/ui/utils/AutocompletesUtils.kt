package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.data.repository.model.Autocomplete
import ru.sokolovromann.myshopping.data.repository.model.Autocompletes
import ru.sokolovromann.myshopping.data.repository.model.formatFirst
import ru.sokolovromann.myshopping.ui.compose.state.AutocompleteItems
import ru.sokolovromann.myshopping.ui.compose.state.UiText

fun Autocompletes.getAutocompleteItems(): Map<UiText, AutocompleteItems> {
    val items: MutableMap<UiText, AutocompleteItems> = mutableMapOf()
    groupAutocompletesByName().forEach {
        val name: UiText = UiText.FromString(it.key.formatFirst(true))
        val autocompletes = toAutocompleteItems(it.value)
        items[name] = autocompletes
    }
    return items
}

private fun Autocompletes.toAutocompleteItems(
    autocompletes: List<Autocomplete>
): AutocompleteItems {

    val brandsList: List<UiText> = autocompletes
        .sortedByDescending { it.lastModified }
        .distinctBy { it.brand }
        .filterIndexed { index, autocomplete ->
            autocomplete.brand.isNotEmpty() && index < getMaxAutocompletesOthers()
        }
        .map { UiText.FromString(it.brand) }

    val sizesList: List<UiText> = autocompletes
        .sortedByDescending { it.lastModified }
        .distinctBy { it.size }
        .filterIndexed { index, autocomplete ->
            autocomplete.size.isNotEmpty() && index < getMaxAutocompletesOthers()
        }
        .map { UiText.FromString(it.size) }

    val colorsList: List<UiText> = autocompletes
        .sortedByDescending { it.lastModified }
        .distinctBy { it.color }
        .filterIndexed { index, autocomplete ->
            autocomplete.color.isNotEmpty() && index < getMaxAutocompletesOthers()
        }
        .map { UiText.FromString(it.color) }

    val manufacturersList: List<UiText> = autocompletes
        .sortedByDescending { it.lastModified }
        .distinctBy { it.manufacturer }
        .filterIndexed { index, autocomplete ->
            autocomplete.manufacturer.isNotEmpty() && index < getMaxAutocompletesOthers()
        }
        .map { UiText.FromString(it.manufacturer) }

    val quantitiesList: List<UiText> = autocompletes
        .sortedByDescending { it.lastModified }
        .distinctBy { it.quantity.getFormattedValue() }
        .filterIndexed { index, autocomplete ->
            autocomplete.quantity.isNotEmpty() && index < getMaxAutocompletesQuantities()
        }
        .map { UiText.FromString(it.quantity.toString()) }

    val pricesList: List<UiText> = if (isDisplayMoney()) {
        autocompletes
            .sortedByDescending { it.lastModified }
            .distinctBy { it.price.getFormattedValue() }
            .filterIndexed { index, autocomplete ->
                autocomplete.price.isNotEmpty() && index < getMaxAutocompletesMoneys()
            }
            .map { UiText.FromString(it.price.toString()) }
    } else {
        listOf()
    }

    val discountsList: List<UiText> = if (isDisplayMoney()) {
        autocompletes
            .sortedByDescending { it.lastModified }
            .distinctBy { it.discount.getFormattedValue() }
            .filterIndexed { index, autocomplete ->
                autocomplete.discount.isNotEmpty() && index < getMaxAutocompletesMoneys()
            }
            .map { UiText.FromString(it.discount.toString()) }
    } else {
        listOf()
    }

    val totalsList: List<UiText> = if (isDisplayMoney()) {
        autocompletes
            .sortedByDescending { it.lastModified }
            .distinctBy { it.total.getFormattedValue() }
            .filterIndexed { index, autocomplete ->
                autocomplete.total.isNotEmpty() && index < getMaxAutocompletesMoneys()
            }
            .map { UiText.FromString(it.total.toString()) }
    } else {
        listOf()
    }

    return AutocompleteItems(
        brandsList = brandsList,
        sizesList = sizesList,
        colorsList = colorsList,
        manufacturersList = manufacturersList,
        quantitiesList = quantitiesList,
        pricesList = pricesList,
        discountsList = discountsList,
        totalsList = totalsList
    )
}