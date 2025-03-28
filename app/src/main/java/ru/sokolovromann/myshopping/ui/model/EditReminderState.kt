package ru.sokolovromann.myshopping.ui.model

import androidx.annotation.StyleRes
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.Shopping
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.data.utils.setDate
import ru.sokolovromann.myshopping.data.utils.setTime
import ru.sokolovromann.myshopping.data.utils.toDateTime
import java.util.Calendar

class EditReminderState {

    private var shoppingListWithConfig by mutableStateOf(ShoppingListWithConfig())

    var calendar: Calendar by mutableStateOf(Calendar.getInstance())
        private set

    var displayPermissionError: Boolean by mutableStateOf(false)
        private set

    var displayDeleteButton: Boolean by mutableStateOf(false)
        private set

    var displayDateDialog: Boolean by mutableStateOf(false)
        private set

    var displayTimeDialog: Boolean by mutableStateOf(false)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(shoppingListWithConfig: ShoppingListWithConfig, correctReminderPermission: Boolean) {
        this.shoppingListWithConfig = shoppingListWithConfig

        val shopping = shoppingListWithConfig.getShopping()
        val reminder = shopping.reminder ?: shopping.createDefaultReminder()
        calendar = reminder.toCalendar()
        displayPermissionError = !correctReminderPermission
        displayDeleteButton = shopping.reminder != null
        displayDateDialog = false
        displayTimeDialog = false
        waiting = false
    }

    fun onDateChanged(year: Int, month: Int, dayOfMonth: Int) {
        calendar = calendar.apply {
            setDate(year, month, dayOfMonth)
        }
        displayDateDialog = false
        displayTimeDialog = false
    }

    fun onSelectDate(display: Boolean) {
        displayDateDialog = display
        displayTimeDialog = false
    }

    fun onTimeChanged(hourOfDay: Int, minute: Int) {
        calendar = calendar.apply {
            setTime(hourOfDay, minute)
        }
        displayDateDialog = false
        displayTimeDialog = false
    }

    fun onSelectTime(display: Boolean) {
        displayTimeDialog = display
        displayDateDialog = false
    }

    fun onWaiting() {
        waiting = true
    }

    fun getCurrentShopping(): Shopping {
        return shoppingListWithConfig.getShopping().copy(
            reminder = calendar.toDateTime()
        )
    }

    @StyleRes
    @Composable
    fun getDialogStyle(): Int {
        return if (MaterialTheme.colors.isLight) {
            R.style.Theme_MyShopping_DateTimePicker_Light
        } else {
            R.style.Theme_MyShopping_DateTimePicker_Dark
        }
    }
}