package ru.sokolovromann.myshopping.ui.viewmodel.event

sealed class MainEvent {

    data class OnCreate(val screenWidth: Int, val screenHeight: Int) : MainEvent()

    data class OnSaveShoppingUid(val uid: String?) : MainEvent()
}