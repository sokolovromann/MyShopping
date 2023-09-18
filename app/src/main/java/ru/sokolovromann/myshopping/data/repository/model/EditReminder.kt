package ru.sokolovromann.myshopping.data.repository.model

import java.util.Calendar

data class EditReminder(
    private val shoppingList: ShoppingList? = null,
    private val appConfig: AppConfig = AppConfig()
) {

    private val _shoppingList = shoppingList ?: ShoppingList()
    private val userPreferences = appConfig.userPreferences

    var reminder = reminderToCalendar()
        private set

    fun createShoppingList(): Result<ShoppingList> {
        val success = _shoppingList.copy(
            reminder = reminder.timeInMillis,
            lastModified = System.currentTimeMillis()
        )
        return Result.success(success)
    }

    fun changeReminderDate(year: Int, month: Int, dayOfMonth: Int) {
        reminder = reminder.apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
    }
    fun changeReminderTime(hourOfDay: Int, minute: Int) {
        reminder = reminder.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
    }

    fun getYear(): Int {
        return reminder.get(Calendar.YEAR)
    }

    fun getMonth(): Int {
        return reminder.get(Calendar.MONTH)
    }

    fun getDayOfMonth(): Int {
        return reminder.get(Calendar.DAY_OF_MONTH)
    }

    fun getHourOfDay(): Int {
        return reminder.get(Calendar.HOUR_OF_DAY)
    }

    fun getMinute(): Int {
        return reminder.get(Calendar.MINUTE)
    }

    fun hasReminder(): Boolean {
        return _shoppingList.reminder != null
    }

    fun isDisplayDeleteReminder(): Boolean {
        return hasReminder()
    }

    fun getFontSize(): FontSize {
        return userPreferences.fontSize
    }

    private fun reminderToCalendar(): Calendar {
        return Calendar.getInstance().apply {
            if (_shoppingList.reminder == null) {
                timeInMillis += 3600000L // + 1 hour
            } else {
                timeInMillis = _shoppingList.reminder
            }

            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
}