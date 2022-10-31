package ru.sokolovromann.myshopping.ui.compose.state


data class ShoppingListsCompletedMenu(
    val title: TextData = TextData.Title,
    val firstBody: TextData = TextData.Body,
    val firstSelected: RadioButtonData = RadioButtonData.OnSurface,
    val lastBody: TextData = TextData.Body,
    val lastSelected: RadioButtonData = RadioButtonData.OnSurface,
    val hideBody: TextData = TextData.Body,
    val hideSelected: RadioButtonData = RadioButtonData.OnSurface
)