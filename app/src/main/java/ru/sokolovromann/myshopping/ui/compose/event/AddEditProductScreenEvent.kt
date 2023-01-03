package ru.sokolovromann.myshopping.ui.compose.event

sealed class AddEditProductScreenEvent {

    object ShowBackScreen : AddEditProductScreenEvent()

    object ShowKeyboard : AddEditProductScreenEvent()
}