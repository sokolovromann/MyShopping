package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.data.repository.model.AppConfig
import ru.sokolovromann.myshopping.data.repository.model.Autocomplete
import ru.sokolovromann.myshopping.data.repository.model.Autocompletes
import ru.sokolovromann.myshopping.data.repository.model.formatFirst
import ru.sokolovromann.myshopping.ui.compose.state.AutocompleteItems
import ru.sokolovromann.myshopping.ui.compose.state.UiText

fun Autocompletes.getAutocompleteItems(): Map<UiText, AutocompleteItems> {
    val items: MutableMap<UiText, AutocompleteItems> = mutableMapOf()
    formatAutocompletes().groupBy { it.name.lowercase() }.forEach {
        val name: UiText = UiText.FromString(it.key.formatFirst(true))
        val autocompletes = toAutocompleteItems(it.value, appConfig)
        items[name] = autocompletes
    }
    return items
}

private fun toAutocompleteItems(
    autocompletes: List<Autocomplete>,
    appConfig: AppConfig
): AutocompleteItems {
    val otherLimit = 3
    val quantitiesLimit = 5
    val pricesLimit = 3
    val discountsLimit = 3
    val totalsLimit = 3

    val brandsList: List<UiText> = autocompletes
        .sortedByDescending { it.lastModified }
        .distinctBy { it.brand }
        .filterIndexed { index, autocomplete ->
            autocomplete.brand.isNotEmpty() && index < otherLimit
        }
        .map { UiText.FromString(it.brand) }

    val sizesList: List<UiText> = autocompletes
        .sortedByDescending { it.lastModified }
        .distinctBy { it.size }
        .filterIndexed { index, autocomplete ->
            autocomplete.size.isNotEmpty() && index < otherLimit
        }
        .map { UiText.FromString(it.size) }

    val colorsList: List<UiText> = autocompletes
        .sortedByDescending { it.lastModified }
        .distinctBy { it.color }
        .filterIndexed { index, autocomplete ->
            autocomplete.color.isNotEmpty() && index < otherLimit
        }
        .map { UiText.FromString(it.color) }

    val manufacturersList: List<UiText> = autocompletes
        .sortedByDescending { it.lastModified }
        .distinctBy { it.manufacturer }
        .filterIndexed { index, autocomplete ->
            autocomplete.manufacturer.isNotEmpty() && index < otherLimit
        }
        .map { UiText.FromString(it.manufacturer) }

    val quantitiesList: List<UiText> = autocompletes
        .sortedByDescending { it.lastModified }
        .distinctBy { it.quantity.formatValue() }
        .filterIndexed { index, autocomplete ->
            autocomplete.quantity.isNotEmpty() && index < quantitiesLimit
        }
        .map { UiText.FromString(it.quantity.toString()) }

    val pricesList: List<UiText> = if (appConfig.userPreferences.displayMoney) {
        autocompletes
            .sortedByDescending { it.lastModified }
            .distinctBy { it.price.getFormattedValue() }
            .filterIndexed { index, autocomplete ->
                autocomplete.price.isNotEmpty() && index < pricesLimit
            }
            .map { UiText.FromString(it.price.toString()) }
    } else {
        listOf()
    }

    val discountsList: List<UiText> = if (appConfig.userPreferences.displayMoney) {
        autocompletes
            .sortedByDescending { it.lastModified }
            .distinctBy { it.discount.getFormattedValue() }
            .filterIndexed { index, autocomplete ->
                autocomplete.discount.isNotEmpty() && index < discountsLimit
            }
            .map { UiText.FromString(it.discount.toString()) }
    } else {
        listOf()
    }

    val totalsList: List<UiText> = if (appConfig.userPreferences.displayMoney) {
        autocompletes
            .sortedByDescending { it.lastModified }
            .distinctBy { it.total.getFormattedValue() }
            .filterIndexed { index, autocomplete ->
                autocomplete.total.isNotEmpty() && index < totalsLimit
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