package ru.sokolovromann.myshopping.ui.compose.event

sealed class MainScreenEvent {

    object GetDefaultDeviceConfig : MainScreenEvent()

    object GetScreenSize : MainScreenEvent()
}