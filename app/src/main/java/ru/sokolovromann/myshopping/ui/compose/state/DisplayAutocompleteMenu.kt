package ru.sokolovromann.myshopping.ui.compose.state

data class DisplayAutocompleteMenu(
    val allBody: TextData = TextData(),
    val allSelected: RadioButtonData = RadioButtonData(),
    val nameBody: TextData = TextData(),
    val nameSelected: RadioButtonData = RadioButtonData(),
    val hideBody: TextData = TextData(),
    val hideSelected: RadioButtonData = RadioButtonData()
)