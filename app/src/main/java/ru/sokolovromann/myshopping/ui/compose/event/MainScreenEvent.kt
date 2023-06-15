package ru.sokolovromann.myshopping.ui.compose.event

sealed class MainScreenEvent {

    object GetDefaultPreferences : MainScreenEvent()

    object GetScreenSize : MainScreenEvent()
}