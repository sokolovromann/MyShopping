package ru.sokolovromann.myshopping.ui.compose.state

data class AutocompletesSortMenu(
    val title: TextData = TextData.Title,
    val byCreatedBody: TextData = TextData.Body,
    val byCreatedSelected: RadioButtonData = RadioButtonData.OnSurface,
    val byNameBody: TextData = TextData.Body,
    val byNameSelected: RadioButtonData = RadioButtonData.OnSurface
)