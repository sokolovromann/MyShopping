package ru.sokolovromann.myshopping.ui.viewmodel.event

sealed class MainEvent {

    data class OnCreate(val shoppingUid: String?) : MainEvent()

    data class AddDefaultPreferences(val screenWidth: Int, val screenHeight: Int) : MainEvent()
}