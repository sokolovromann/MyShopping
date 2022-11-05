package ru.sokolovromann.myshopping.ui.compose.state

data class ShoppingListItem(
    val uid: String = "",
    val title: TextData = TextData(),
    val titleIcon: IconData = IconData(),
    val productsBody: List<Pair<IconData, TextData>> = listOf(),
    val totalBody: TextData = TextData(),
    val reminderBody: TextData = TextData()
)