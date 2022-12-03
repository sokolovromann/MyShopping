package ru.sokolovromann.myshopping.ui.compose.state

data class DiscountAsPercentMenu(
    val asPercentBody: TextData = TextData(),
    val asPercentSelected: RadioButtonData = RadioButtonData(),
    val asMoneyBody: TextData = TextData(),
    val asMoneySelected: RadioButtonData = RadioButtonData(),
)