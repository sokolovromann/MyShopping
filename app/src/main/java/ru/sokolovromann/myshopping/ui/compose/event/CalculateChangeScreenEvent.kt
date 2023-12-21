package ru.sokolovromann.myshopping.ui.compose.event

sealed class CalculateChangeScreenEvent {

    object OnShowBackScreen : CalculateChangeScreenEvent()

    object OnShowKeyboard : CalculateChangeScreenEvent()
}