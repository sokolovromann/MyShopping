package ru.sokolovromann.myshopping.core.domain.model

sealed class SwipeProduct {

    data class Left(val actionName: SwipeProductActionName) : SwipeProduct()

    data class Right(val actionName: SwipeProductActionName) : SwipeProduct()
}