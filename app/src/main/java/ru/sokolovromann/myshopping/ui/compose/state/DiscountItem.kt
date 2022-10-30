package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.data.repository.model.Discount

data class DiscountItem(
    val discount: Discount = Discount(),
    val text: TextData = TextData()
)