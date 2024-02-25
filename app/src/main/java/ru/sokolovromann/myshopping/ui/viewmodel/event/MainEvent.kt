package ru.sokolovromann.myshopping.ui.viewmodel.event

sealed class MainEvent {

    data class OnCreate(val screenWidth: Int, val screenHeight: Int) : MainEvent()

    data class OnSaveIntent(val action: String?, val uid: String?) : MainEvent()
}