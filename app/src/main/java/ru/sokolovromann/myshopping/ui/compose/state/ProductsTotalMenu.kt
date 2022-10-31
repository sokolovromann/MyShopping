package ru.sokolovromann.myshopping.ui.compose.state

data class ProductsTotalMenu(
    val title: TextData = TextData.Title,
    val allBody: TextData = TextData.Body,
    val allSelected: RadioButtonData = RadioButtonData.OnSurface,
    val completedBody: TextData = TextData.Body,
    val completedSelected: RadioButtonData = RadioButtonData.OnSurface,
    val activeBody: TextData = TextData.Body,
    val activeSelected: RadioButtonData = RadioButtonData.OnSurface,
    val hideBody: TextData = TextData.Body,
    val hideSelected: RadioButtonData = RadioButtonData.OnSurface
)