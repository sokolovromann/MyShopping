package ru.sokolovromann.myshopping.ui.compose.state

data class TrashItemMenu(
    val moveToPurchasesBody: TextData = TextData(),
    val moveToArchiveBody: TextData = TextData(),
    val deleteBody: TextData = TextData(),
)