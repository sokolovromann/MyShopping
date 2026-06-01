package ru.sokolovromann.myshopping.core.domain.model

sealed class Currency {

    data class Left(val sign: String) : Currency()

    data class Right(val sign: String) : Currency()
}