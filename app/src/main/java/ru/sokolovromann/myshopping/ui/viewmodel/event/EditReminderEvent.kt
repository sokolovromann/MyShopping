package ru.sokolovromann.myshopping.ui.viewmodel.event

sealed class EditReminderEvent {

    object OnClickSave : EditReminderEvent()

    object OnClickCancel : EditReminderEvent()

    object OnClickDelete : EditReminderEvent()

    object OnClickOpenPermissions : EditReminderEvent()

    data class OnDateChanged(val year: Int, val month: Int, val dayOfMonth: Int) : EditReminderEvent()

    data class OnSelectDate(val display: Boolean) : EditReminderEvent()

    data class OnTimeChanged(val hourOfDay: Int, val minute: Int) : EditReminderEvent()

    data class OnSelectTime(val display: Boolean) : EditReminderEvent()
}