package ru.sokolovromann.myshopping.ui.compose.state

data class ProductsSortMenu(
    val title: TextData = TextData(),
    val byCreatedBody: TextData = TextData(),
    val byCreatedSelected: RadioButtonData = RadioButtonData(),
    val byLastModifiedBody: TextData = TextData(),
    val byLastModifiedSelected: RadioButtonData = RadioButtonData(),
    val byNameBody: TextData = TextData(),
    val byNameSelected: RadioButtonData = RadioButtonData(),
    val byTotalBody: TextData = TextData(),
    val byTotalSelected: RadioButtonData = RadioButtonData()
)