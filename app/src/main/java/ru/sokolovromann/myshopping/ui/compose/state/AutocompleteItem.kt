package ru.sokolovromann.myshopping.ui.compose.state

data class AutocompleteItem(
    val uid: String = "",
    val title: TextData = TextData(),
    val body: TextData = TextData()
)