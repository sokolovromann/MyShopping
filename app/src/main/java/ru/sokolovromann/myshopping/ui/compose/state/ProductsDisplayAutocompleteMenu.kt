package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.data.repository.model.DisplayAutocomplete

data class ProductsDisplayAutocompleteMenu(
    val allBody: TextData = TextData.Body,
    val nameBody: TextData = TextData.Body,
    val hideBody: TextData = TextData.Body,
    val selected: DisplayAutocomplete = DisplayAutocomplete.DefaultValue
)