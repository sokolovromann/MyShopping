package ru.sokolovromann.myshopping.ui.compose.state

data class ShoppingListsLocationMenu(
    val title: TextData = TextData(),
    val purchasesBody: TextData = TextData(),
    val purchasesSelected: RadioButtonData = RadioButtonData(),
    val archiveBody: TextData = TextData(),
    val archiveSelected: RadioButtonData = RadioButtonData(),
    val trashBody: TextData = TextData(),
    val trashSelected: RadioButtonData = RadioButtonData(),
)