package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal

data class ProductsTotalMenu(
    val title: TextData = TextData.Title,
    val allBody: TextData = TextData.Body,
    val completedBody: TextData = TextData.Body,
    val activeBody: TextData = TextData.Body,
    val hideBody: TextData = TextData.Body,
    val selected: DisplayTotal = DisplayTotal.DefaultValue
)