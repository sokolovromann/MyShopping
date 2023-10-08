package ru.sokolovromann.myshopping.data.repository.model

import java.util.Calendar

class Time(val millis: Long) {

    companion object {
        fun getCurrentTime(): Time {
            return Time(millis = System.currentTimeMillis())
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

    override fun toString(): String {
        return toCalendar().time.toString()
    }
}