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
import ru.sokolovromann.myshopping.data.model.DateTime
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.data.utils.getHourMinute
import ru.sokolovromann.myshopping.data.utils.getYearMonthDay
import ru.sokolovromann.myshopping.data.utils.setDate
import ru.sokolovromann.myshopping.data.utils.setTime
import ru.sokolovromann.myshopping.data.utils.toDateTime
import ru.sokolovromann.myshopping.ui.utils.getDisplayDate
import ru.sokolovromann.myshopping.ui.utils.getDisplayTime
import java.util.Calendar

class EditReminderState {

    private var shoppingListWithConfig by mutableStateOf(ShoppingListWithConfig())

    private var calendar by mutableStateOf(Calendar.getInstance())

    var screenData by mutableStateOf(EditReminderScreenData())
        private set

    fun populate(shoppingListWithConfig: ShoppingListWithConfig, correctReminderPermission: Boolean) {
        this.shoppingListWithConfig = shoppingListWithConfig

        val shopping = shoppingListWithConfig.shoppingList.shopping
        val headerText: UiText = if (shopping.reminder != null) {
            UiText.FromResources(R.string.editReminder_header_editReminder)
        } else {
            UiText.FromResources(R.string.editReminder_header_addReminder)
        }

        val reminder = (shopping.reminder ?: shopping.createDefaultReminder()).toCalendar()
        calendar = reminder

        val yearMonthDay = reminder.getYearMonthDay()
        val hourMinute = reminder.getHourMinute()
        screenData = EditReminderScreenData(
            screenState = ScreenState.Showing,
            headerText = headerText,
            dateText = reminder.getDisplayDate(),
            dateYear = yearMonthDay.first,
            dateMonth = yearMonthDay.second,
            dateDayOfMonth = yearMonthDay.third,
            timeText = reminder.getDisplayTime(),
            timeHourOfDay = hourMinute.first,
            timeMinute = hourMinute.second,
            showPermissionError = !correctReminderPermission,
            showDeleteButton = shopping.reminder != null,
            showDateDialog = false,
            showTimeDialog = false,
            fontSize = shoppingListWithConfig.appConfig.userPreferences.fontSize
        )
    }

    fun changeReminderDate(year: Int, month: Int, dayOfMonth: Int) {
        calendar = calendar.apply {
            setDate(year, month, dayOfMonth)
        }

        val yearMonthDay = calendar.getYearMonthDay()
        screenData = screenData.copy(
            dateText = calendar.getDisplayDate(),
            dateYear = yearMonthDay.first,
            dateMonth = yearMonthDay.second,
            dateDayOfMonth = yearMonthDay.third,
            showDateDialog = false,
            showTimeDialog = false
        )
    }

    fun changeReminderTime(hourOfDay: Int, minute: Int) {
        calendar = calendar.apply {
            setTime(hourOfDay, minute)
        }

        val hourMinute = calendar.getHourMinute()
        screenData = screenData.copy(
            timeText = calendar.getDisplayTime(),
            timeHourOfDay = hourMinute.first,
            timeMinute = hourMinute.second,
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

    fun getShoppingUid(): String {
        return shoppingListWithConfig.shoppingList.shopping.uid
    }

    fun getReminder(): DateTime {
        return calendar.toDateTime()
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