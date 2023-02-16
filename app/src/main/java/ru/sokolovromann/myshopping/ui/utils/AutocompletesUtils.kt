package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.data.repository.model.Autocompletes
import ru.sokolovromann.myshopping.ui.compose.state.AutocompleteItem
import ru.sokolovromann.myshopping.ui.compose.state.UiText

fun Autocompletes.getAutocompleteItems(): List<AutocompleteItem> {
    return formatAutocompletes().map {
        AutocompleteItem(
            uid = it.uid,
            nameText = UiText.FromString(it.name)
        )
    }
}