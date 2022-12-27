package ru.sokolovromann.myshopping.ui.compose.event

sealed class AddEditAutocompleteScreenEvent {

    object ShowBackScreen : AddEditAutocompleteScreenEvent()

    object ShowKeyboard : AddEditAutocompleteScreenEvent()
}