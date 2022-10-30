package ru.sokolovromann.myshopping.ui.compose.state

data class ShoppingListItem(
    val uid: String = "",
    val title: TextData = TextData.Title,
    val titleIcon: IconData = IconData.OnSurface,
    val productsBody: Pair<IconData, TextData> = Pair(IconData.OnSurface, TextData.Body),
    val totalBody: TextData = TextData.Body,
    val reminderBody: TextData = TextData.Body
)