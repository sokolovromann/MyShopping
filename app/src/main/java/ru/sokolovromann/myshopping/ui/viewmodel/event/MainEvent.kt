package ru.sokolovromann.myshopping.ui.viewmodel.event

sealed class MainEvent {

    object OnCreate : MainEvent()

    data class OnStart(val shoppingUid: String?) : MainEvent()

    object OnStop : MainEvent()

    data class AddDefaultDeviceConfig(val screenWidth: Int, val screenHeight: Int) : MainEvent()

    data class MigrateFromCodeVersion14(val screenWidth: Int, val screenHeight: Int) : MainEvent()
}