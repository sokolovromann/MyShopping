package ru.sokolovromann.myshopping.data.repository.model

import java.util.Calendar

data class EditReminder(
    val shoppingList: ShoppingList? = null,
    val appConfig: AppConfig = AppConfig()
) {

    fun hasReminder(): Boolean {
        return shoppingList?.reminder != null
    }

    fun reminderToCalendar(plusMillisIfNull: Long = 3600000L): Calendar {
        return Calendar.getInstance().apply {
            if (shoppingList?.reminder == null) {
                timeInMillis += plusMillisIfNull
            } else {
                timeInMillis = shoppingList.reminder
            }

            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
}