package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.data.utils.addTime
import ru.sokolovromann.myshopping.data.utils.getHourMinute
import ru.sokolovromann.myshopping.data.utils.toDateTime
import java.util.Calendar

data class Shopping(
    val id: Int = IdDefaults.NO_ID,
    val position: Int = IdDefaults.FIRST_POSITION,
    val uid: String = IdDefaults.createUid(),
    val lastModified: DateTime = DateTime.getCurrentDateTime(),
    val name: String = "",
    val reminder: DateTime? = null,
    val discount: Money = Money(),
    val discountProducts: DisplayTotal = DisplayTotal.DefaultValue,
    val total: Money = Money(),
    val totalFormatted: Boolean = false,
    val budget: Money = Money(),
    val budgetProducts: DisplayTotal = DisplayTotal.DefaultValue,
    val location: ShoppingLocation = ShoppingLocation.DefaultValue,
    val sort: Sort = Sort(),
    val sortFormatted: Boolean = false,
    val pinned: Boolean = false
) {

    fun createDefaultReminder(): DateTime {
        val calendar = Calendar.getInstance().apply {
            val hourMinute = getHourMinute()
            val newMinute = 60 - hourMinute.second
            addTime(1, newMinute)
        }

        return calendar.toDateTime()
    }
}