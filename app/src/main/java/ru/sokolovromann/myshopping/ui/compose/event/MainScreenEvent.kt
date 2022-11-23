package ru.sokolovromann.myshopping.ui.compose.event

sealed class MainScreenEvent {

    data class ShowProducts(val uid: String) : MainScreenEvent()

    object GetDefaultPreferences : MainScreenEvent()

    object GetScreenSize : MainScreenEvent()
}