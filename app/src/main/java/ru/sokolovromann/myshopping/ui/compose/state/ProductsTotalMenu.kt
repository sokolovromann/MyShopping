package ru.sokolovromann.myshopping.ui.compose.state

data class ProductsTotalMenu(
    val title: TextData = TextData(),
    val allBody: TextData = TextData(),
    val allSelected: RadioButtonData = RadioButtonData(),
    val completedBody: TextData = TextData(),
    val completedSelected: RadioButtonData = RadioButtonData(),
    val activeBody: TextData = TextData(),
    val activeSelected: RadioButtonData = RadioButtonData(),
    val hideBody: TextData = TextData(),
    val hideSelected: RadioButtonData = RadioButtonData()
)