package ru.sokolovromann.myshopping.ui.viewmodel.event

sealed class MainEvent {

    object OnCreate : MainEvent()

    data class OnStart(val shoppingUid: String?) : MainEvent()

    object OnStop : MainEvent()

    data class AddDefaultPreferences(val screenWidth: Int, val screenHeight: Int) : MainEvent()

    data class MigrateFromAppVersion14(val screenWidth: Int, val screenHeight: Int) : MainEvent()
}