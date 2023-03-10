package ru.sokolovromann.myshopping.ui.compose.state

data class AutocompleteItems(
    val quantitiesList: List<UiText> = listOf(),
    val pricesList: List<UiText> = listOf(),
    val discountsList: List<UiText> = listOf(),
    val totalsList: List<UiText> = listOf()
)