package ru.sokolovromann.myshopping.ui.compose.event

sealed class EditReminderScreenEvent {

    object ShowBackScreen : EditReminderScreenEvent()

    data class ShowPermissions(val packageName: String) : EditReminderScreenEvent()
}