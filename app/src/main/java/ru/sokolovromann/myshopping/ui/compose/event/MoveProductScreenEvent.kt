package ru.sokolovromann.myshopping.ui.compose.event

sealed class MoveProductScreenEvent {

    object ShowBackScreen : MoveProductScreenEvent()

    object ShowBackScreenAndUpdateProductsWidgets : MoveProductScreenEvent()
}