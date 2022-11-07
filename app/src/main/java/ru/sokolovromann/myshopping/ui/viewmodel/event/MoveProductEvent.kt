package ru.sokolovromann.myshopping.ui.viewmodel.event

sealed class MoveProductEvent {

    data class MoveProduct(val uid: String) : MoveProductEvent()

    object ShowBackScreen : MoveProductEvent()
}