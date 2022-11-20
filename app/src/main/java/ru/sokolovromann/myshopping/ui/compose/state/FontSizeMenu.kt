package ru.sokolovromann.myshopping.ui.compose.state

data class FontSizeMenu(
    val tinyBody: TextData = TextData(),
    val tinySelected: RadioButtonData = RadioButtonData(),
    val smallBody: TextData = TextData(),
    val smallSelected: RadioButtonData = RadioButtonData(),
    val mediumBody: TextData = TextData(),
    val mediumSelected: RadioButtonData = RadioButtonData(),
    val largeBody: TextData = TextData(),
    val largeSelected: RadioButtonData = RadioButtonData(),
    val hugeBody: TextData = TextData(),
    val hugeSelected: RadioButtonData = RadioButtonData()
)