package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted

data class ShoppingListsCompletedMenu(
    val title: TextData = TextData.Title,
    val firstBody: TextData = TextData.Body,
    val lastBody: TextData = TextData.Body,
    val hideBody: TextData = TextData.Body,
    val selected: DisplayCompleted = DisplayCompleted.DefaultValue
)