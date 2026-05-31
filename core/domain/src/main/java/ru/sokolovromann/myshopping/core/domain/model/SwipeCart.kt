package ru.sokolovromann.myshopping.core.domain.model

sealed class SwipeCart {

    data class Left(val actionName: SwipeCartActionName) : SwipeCart()

    data class Right(val actionName: SwipeCartActionName) : SwipeCart()
}