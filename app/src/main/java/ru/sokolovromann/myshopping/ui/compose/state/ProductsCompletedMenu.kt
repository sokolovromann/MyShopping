package ru.sokolovromann.myshopping.ui.compose.state

data class ProductsCompletedMenu(
    val title: TextData = TextData(),
    val firstBody: TextData = TextData(),
    val firstSelected: RadioButtonData = RadioButtonData(),
    val lastBody: TextData = TextData(),
    val lastSelected: RadioButtonData = RadioButtonData(),
    val hideBody: TextData = TextData(),
    val hideSelected: RadioButtonData = RadioButtonData()
)