package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.AutocompletesWithConfig
import ru.sokolovromann.myshopping.ui.compose.state.AutocompleteItems
import ru.sokolovromann.myshopping.ui.compose.state.UiText

fun AutocompletesWithConfig.getAutocompletesItems(): Map<UiText, AutocompleteItems> {
    val items: MutableMap<UiText, AutocompleteItems> = mutableMapOf()
    groupAutocompletesByName().forEach {
        val name: UiText = UiText.FromString(it.key)
        val autocompletes = toAutocompleteItems(it.value)
        items[name] = autocompletes
    }

    return items
}

private fun AutocompletesWithConfig.toAutocompleteItems(
    autocompletes: List<Autocomplete>
): AutocompleteItems {
    val userPreferences = appConfig.userPreferences
    val brandsList: List<UiText> = autocompletes
        .sortedByDescending { it.lastModified.millis }
        .distinctBy { it.brand }
        .filterIndexed { index, autocomplete ->
            autocomplete.brand.isNotEmpty() && index < userPreferences.maxAutocompletesOthers
        }
        .map { UiText.FromString(it.brand) }

    val sizesList: List<UiText> = autocompletes
        .sortedByDescending { it.lastModified.millis }
        .distinctBy { it.size }
        .filterIndexed { index, autocomplete ->
            autocomplete.size.isNotEmpty() && index < userPreferences.maxAutocompletesOthers
        }
        .map { UiText.FromString(it.size) }

    val colorsList: List<UiText> = autocompletes
        .sortedByDescending { it.lastModified.millis  }
        .distinctBy { it.color }
        .filterIndexed { index, autocomplete ->
            autocomplete.color.isNotEmpty() && index < userPreferences.maxAutocompletesOthers
        }
        .map { UiText.FromString(it.color) }

    val manufacturersList: List<UiText> = autocompletes
        .sortedByDescending { it.lastModified.millis  }
        .distinctBy { it.manufacturer }
        .filterIndexed { index, autocomplete ->
            autocomplete.manufacturer.isNotEmpty() && index < userPreferences.maxAutocompletesOthers
        }
        .map { UiText.FromString(it.manufacturer) }

    val quantitiesList: List<UiText> = autocompletes
        .sortedByDescending { it.lastModified.millis  }
        .distinctBy { it.quantity.getFormattedValue() }
        .filterIndexed { index, autocomplete ->
            autocomplete.quantity.isNotEmpty() && index < userPreferences.maxAutocompletesQuantities
        }
        .map { UiText.FromString(it.quantity.toString()) }

    val pricesList: List<UiText> = if (userPreferences.displayMoney) {
        autocompletes
            .sortedByDescending { it.lastModified.millis }
            .distinctBy { it.price.getFormattedValue() }
            .filterIndexed { index, autocomplete ->
                autocomplete.price.isNotEmpty() && index < userPreferences.maxAutocompletesMoneys
            }
            .map { UiText.FromString(it.price.toString()) }
    } else {
        listOf()
    }

    val discountsList: List<UiText> = if (userPreferences.displayMoney) {
        autocompletes
            .sortedByDescending { it.lastModified.millis  }
            .distinctBy { it.discount.getFormattedValue() }
            .filterIndexed { index, autocomplete ->
                autocomplete.discount.isNotEmpty() && index < userPreferences.maxAutocompletesMoneys
            }
            .map { UiText.FromString(it.discount.toString()) }
    } else {
        listOf()
    }

    val totalsList: List<UiText> = if (userPreferences.displayMoney) {
        autocompletes
            .sortedByDescending { it.lastModified.millis  }
            .distinctBy { it.total.getFormattedValue() }
            .filterIndexed { index, autocomplete ->
                autocomplete.total.isNotEmpty() && index < userPreferences.maxAutocompletesMoneys
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