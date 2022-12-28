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

    private var shoppingList by mutableStateOf(ShoppingList())

    private var reminder by mutableStateOf(Calendar.getInstance())

    var screenData by mutableStateOf(EditReminderScreenData())
        private set

    fun populate(editReminder: EditReminder) {
        shoppingList = editReminder.shoppingList ?: ShoppingList()

        val headerText: UiText = if (shoppingList.reminder == null) {
            UiText.FromResources(R.string.editReminder_header_addReminder)
        } else {
            UiText.FromResources(R.string.editReminder_header_editReminder)
        }

        reminder = Calendar.getInstance().apply {
            if (shoppingList.reminder == null) {
                timeInMillis += 3600000L // plus 1 hour
            } else {
                timeInMillis = shoppingList.reminder!!
            }

            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        screenData = EditReminderScreenData(
            headerText = headerText,
            dateText = reminder.getDisplayDate(),
            dateYear = reminder.get(Calendar.YEAR),
            dateMonth = reminder.get(Calendar.MONTH),
            dateDayOfMonth = reminder.get(Calendar.DAY_OF_MONTH),
            timeText = reminder.getDisplayTime(),
            timeHourOfDay = reminder.get(Calendar.HOUR_OF_DAY),
            timeMinute = reminder.get(Calendar.MINUTE),
            showDeleteButton = shoppingList.reminder != null,
            showDateDialog = false,
            showTimeDialog = false,
            fontSize = editReminder.preferences.fontSize
        )
    }

    fun changeReminderDate(year: Int, month: Int, dayOfMonth: Int) {
        reminder = reminder.apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }

        screenData = screenData.copy(
            dateText = reminder.getDisplayDate(),
            dateYear = reminder.get(Calendar.YEAR),
            dateMonth = reminder.get(Calendar.MONTH),
            dateDayOfMonth = reminder.get(Calendar.DAY_OF_MONTH),
            showDateDialog = false,
            showTimeDialog = false
        )
    }

    fun changeReminderTime(hourOfDay: Int, minute: Int) {
        reminder = reminder.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }

        screenData = screenData.copy(
            timeText = reminder.getDisplayTime(),
            timeHourOfDay = reminder.get(Calendar.HOUR_OF_DAY),
            timeMinute = reminder.get(Calendar.MINUTE),
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
        val success = shoppingList.copy(
            reminder = reminder.timeInMillis,
            lastModified = System.currentTimeMillis()
        )
        return Result.success(success)
    }
}

data class EditReminderScreenData(
    val headerText: UiText = UiText.Nothing,
    val dateText: UiText = UiText.Nothing,
    val dateYear: Int = 0,
    val dateMonth: Int = 0,
    val dateDayOfMonth: Int = 0,
    val timeText: UiText = UiText.Nothing,
    val timeHourOfDay: Int = 0,
    val timeMinute: Int = 0,
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