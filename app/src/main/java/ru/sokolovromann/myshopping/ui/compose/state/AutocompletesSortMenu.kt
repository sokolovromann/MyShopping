package ru.sokolovromann.myshopping.ui.compose.state

data class AutocompletesSortMenu(
    val title: TextData = TextData(),
    val byCreatedBody: TextData = TextData(),
    val byCreatedSelected: RadioButtonData = RadioButtonData(),
    val byNameBody: TextData = TextData(),
    val byNameSelected: RadioButtonData = RadioButtonData()
)