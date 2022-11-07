package ru.sokolovromann.myshopping.ui.viewmodel.event

sealed class EditReminderEvent {

    object SaveReminder : EditReminderEvent()

    object CancelSavingReminder : EditReminderEvent()

    object CancelSelectingReminderDate : EditReminderEvent()

    object CancelSelectingReminderTime : EditReminderEvent()

    object DeleteReminder : EditReminderEvent()

    object SelectReminderDate : EditReminderEvent()

    object SelectReminderTime : EditReminderEvent()

    data class ReminderDateChanged(val year: Int, val month: Int, val dayOfMonth: Int) : EditReminderEvent()

    data class ReminderTimeChanged(val hourOfDay: Int, val minute: Int) : EditReminderEvent()
}