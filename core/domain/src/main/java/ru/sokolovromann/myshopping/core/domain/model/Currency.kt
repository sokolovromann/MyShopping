package ru.sokolovromann.myshopping.core.domain.model

sealed class Currency {

    data class Left(val currencySign: String) : Currency()

    data class Right(val currencySign: String) : Currency()

    fun getSign(): String = when (this) {
        is Left -> currencySign
        is Right -> currencySign
    }
}