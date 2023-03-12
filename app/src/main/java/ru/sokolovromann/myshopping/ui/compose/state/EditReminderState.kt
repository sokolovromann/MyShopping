package ru.sokolovromann.myshopping.ui.compose.state

import android.text.format.DateFormat
import androidx.annotation.StyleRes
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.EditReminder
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.ShoppingList
import ru.sokolovromann.myshopping.ui.utils.getDisplayDate
import ru.sokolovromann.myshopping.ui.utils.getDisplayTime
import java.util.*

class EditReminderState {

    private var editReminder by mutableStateOf(EditReminder())

    private var reminderCalendar by mutableStateOf(Calendar.getInstance())

    var screenData by mutableStateOf(EditReminderScreenData())
        private set

    fun populate(editReminder: EditReminder, correctReminderPermission: Boolean) {
        this.editReminder = editReminder

        val headerText: UiText = if (editReminder.hasReminder()) {
            UiText.FromResources(R.string.editReminder_header_editReminder)
        } else {
            UiText.FromResources(R.string.editReminder_header_addReminder)
        }

        reminderCalendar = editReminder.reminderToCalendar()

        screenData = EditReminderScreenData(
            screenState = ScreenState.Showing,
            headerText = headerText,
            dateText = reminderCalendar.getDisplayDate(),
            dateYear = reminderCalendar.get(Calendar.YEAR),
            dateMonth = reminderCalendar.get(Calendar.MONTH),
            dateDayOfMonth = reminderCalendar.get(Calendar.DAY_OF_MONTH),
            timeText = reminderCalendar.getDisplayTime(),
            timeHourOfDay = reminderCalendar.get(Calendar.HOUR_OF_DAY),
            timeMinute = reminderCalendar.get(Calendar.MINUTE),
            showPermissionError = !correctReminderPermission,
            showDeleteButton = editReminder.hasReminder(),
            showDateDialog = false,
            showTimeDialog = false,
            fontSize = editReminder.preferences.fontSize
        )
    }

    fun changeReminderDate(year: Int, month: Int, dayOfMonth: Int) {
        reminderCalendar = reminderCalendar.apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }

        screenData = screenData.copy(
            dateText = reminderCalendar.getDisplayDate(),
            dateYear = reminderCalendar.get(Calendar.YEAR),
            dateMonth = reminderCalendar.get(Calendar.MONTH),
            dateDayOfMonth = reminderCalendar.get(Calendar.DAY_OF_MONTH),
            showDateDialog = false,
            showTimeDialog = false
        )
    }

    fun changeReminderTime(hourOfDay: Int, minute: Int) {
        reminderCalendar = reminderCalendar.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }

        screenData = screenData.copy(
            timeText = reminderCalendar.getDisplayTime(),
            timeHourOfDay = reminderCalendar.get(Calendar.HOUR_OF_DAY),
            timeMinute = reminderCalendar.get(Calendar.MINUTE),
            showDateDialog = false,
            showTimeDialog = false
        )
    }

    fun selectReminderDate() {
        screenData = screenData.copy(
            showDateDialog = true,
            showTimeDialog = false
        )
    }

    fun selectReminderTime() {
        screenData = screenData.copy(
            showDateDialog = false,
            showTimeDialog = true
        )
    }

    fun cancelSelectingReminderDate() {
        screenData = screenData.copy(
            showDateDialog = false,
            showTimeDialog = false
        )
    }

    fun cancelSelectingReminderTime() {
        screenData = screenData.copy(
            showDateDialog = false,
            showTimeDialog = false
        )
    }

    fun getShoppingListResult(): Result<ShoppingList> {
        screenData = screenData.copy(screenState = ScreenState.Saving)
        val success = (editReminder.shoppingList ?: ShoppingList()).copy(
            reminder = reminderCalendar.timeInMillis,
            lastModified = System.currentTimeMillis()
        )
        return Result.success(success)
    }
}

data class EditReminderScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val headerText: UiText = UiText.Nothing,
    val dateText: UiText = UiText.Nothing,
    val dateYear: Int = 0,
    val dateMonth: Int = 0,
    val dateDayOfMonth: Int = 0,
    val timeText: UiText = UiText.Nothing,
    val timeHourOfDay: Int = 0,
    val timeMinute: Int = 0,
    val showPermissionError: Boolean = false,
    val showDeleteButton: Boolean = false,
    val showDateDialog: Boolean = false,
    val showTimeDialog: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM
) {

    @Composable
    fun isTime24HourFormat(): Boolean {
        return DateFormat.is24HourFormat(LocalContext.current)
    }

    @StyleRes
    @Composable
    fun dateTimeDialogStyle(): Int {
        return if (MaterialTheme.colors.isLight) {
            R.style.Theme_MyShopping_DateTimePicket_Light
        } else {
            R.style.Theme_MyShopping_DateTimePicket_Dark
        }
    }
}