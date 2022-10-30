package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.data.repository.model.Quantity

data class QuantityItem(
    val quantity: Quantity = Quantity(),
    val text: TextData = TextData()
)