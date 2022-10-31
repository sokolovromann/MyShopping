package ru.sokolovromann.myshopping.ui.compose.state

data class ProductsDisplayAutocompleteMenu(
    val allBody: TextData = TextData.Body,
    val allSelected: RadioButtonData = RadioButtonData.OnSurface,
    val nameBody: TextData = TextData.Body,
    val nameSelected: RadioButtonData = RadioButtonData.OnSurface,
    val hideBody: TextData = TextData.Body,
    val hideSelected: RadioButtonData = RadioButtonData.OnSurface
)