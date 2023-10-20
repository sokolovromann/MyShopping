package ru.sokolovromann.myshopping.data.model

import java.util.Calendar

class DateTime(val millis: Long) {

    companion object {
        val NO_DATE_TIME = DateTime(0)

        fun getCurrentDateTime(): DateTime {
            return DateTime(millis = System.currentTimeMillis())
        }
    }

    fun toCalendar(setZeroSeconds: Boolean = true): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = millis

            if (setZeroSeconds) {
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }
    }

    fun getFormattedMillis(): String {
        val format = "%tY%tm%td_%tH%tM%tS"
        return String.format(
            format,
            arrayOf(millis, millis, millis, millis, millis, millis)
        )
    }

    override fun toString(): String {
        return getFormattedMillis()
    }
}