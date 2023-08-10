package ru.sokolovromann.myshopping.data.repository.model

sealed class AppFloatType {

    data class Money(val currency: Currency) : AppFloatType()

    data class Quantity(val symbol: String) : AppFloatType()

    object Percent : AppFloatType()
}