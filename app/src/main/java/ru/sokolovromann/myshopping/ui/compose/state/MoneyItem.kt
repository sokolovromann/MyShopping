package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.data.repository.model.Money

data class MoneyItem(
    val money: Money = Money(),
    val text: TextData = TextData()
)