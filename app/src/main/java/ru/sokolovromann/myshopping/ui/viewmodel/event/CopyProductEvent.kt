package ru.sokolovromann.myshopping.ui.viewmodel.event

sealed class CopyProductEvent {

    data class CopyProduct(val uid: String) : CopyProductEvent()

    object ShowBackScreen : CopyProductEvent()
}