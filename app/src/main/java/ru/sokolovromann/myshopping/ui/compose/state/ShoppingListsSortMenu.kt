package ru.sokolovromann.myshopping.ui.compose.state

data class ShoppingListsSortMenu(
    val title: TextData = TextData.Title,
    val byCreatedBody: TextData = TextData.Body,
    val byCreatedSelected: RadioButtonData = RadioButtonData.OnSurface,
    val byLastModifiedBody: TextData = TextData.Body,
    val byLastModifiedSelected: RadioButtonData = RadioButtonData.OnSurface,
    val byNameBody: TextData = TextData.Body,
    val byNameSelected: RadioButtonData = RadioButtonData.OnSurface,
    val byTotalBody: TextData = TextData.Body,
    val byTotalSelected: RadioButtonData = RadioButtonData.OnSurface
)