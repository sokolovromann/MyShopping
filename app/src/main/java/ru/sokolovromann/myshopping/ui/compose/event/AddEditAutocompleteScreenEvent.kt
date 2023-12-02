package ru.sokolovromann.myshopping.ui.compose.event

sealed class AddEditAutocompleteScreenEvent {

    object OnShowBackScreen : AddEditAutocompleteScreenEvent()

    object OnShowKeyboard : AddEditAutocompleteScreenEvent()
}