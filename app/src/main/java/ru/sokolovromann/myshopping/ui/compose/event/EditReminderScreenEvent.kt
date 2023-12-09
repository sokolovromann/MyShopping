package ru.sokolovromann.myshopping.ui.compose.event

sealed class EditReminderScreenEvent {

    object OnShowBackScreen : EditReminderScreenEvent()

    data class OnShowPermissions(val packageName: String) : EditReminderScreenEvent()
}