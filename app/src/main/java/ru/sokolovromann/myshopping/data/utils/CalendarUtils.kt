package ru.sokolovromann.myshopping.data.utils

import ru.sokolovromann.myshopping.data.model.DateTime
import java.util.Calendar

fun Calendar.setDate(year: Int, month: Int, dayOfMonth: Int) {
    set(Calendar.YEAR, year)
    set(Calendar.MONTH, month)
    set(Calendar.DAY_OF_MONTH, dayOfMonth)
}

fun Calendar.setTime(hourOfDay: Int, minute: Int) {
    set(Calendar.HOUR_OF_DAY, hourOfDay)
    set(Calendar.MINUTE, minute)
}

fun Calendar.addDate(year: Int, month: Int, dayOfMonth: Int) {
    add(Calendar.YEAR, year)
    add(Calendar.MONTH, month)
    add(Calendar.DAY_OF_MONTH, dayOfMonth)
}

fun Calendar.addTime(hourOfDay: Int, minute: Int) {
    add(Calendar.HOUR_OF_DAY, hourOfDay)
    add(Calendar.MINUTE, minute)
}

fun Calendar.getYearMonthDay(): Triple<Int, Int, Int> {
    return Triple(
        first = get(Calendar.YEAR),
        second = get(Calendar.MONTH),
        third = get(Calendar.DAY_OF_MONTH)
    )
}

fun Calendar.getHourMinute(): Pair<Int, Int> {
    return Pair(
        first = get(Calendar.HOUR_OF_DAY),
        second = get(Calendar.MINUTE)
    )
}

fun Calendar.toDateTime(): DateTime {
    return DateTime(timeInMillis)
}

fun Calendar.isCurrentYear(): Boolean {
    return get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
}

fun Calendar.isTomorrow(): Boolean {
    val calendar = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 1)
    }
    return isCurrentYear() && get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)
}

fun Calendar.isToday(): Boolean {
    return isCurrentYear() && get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
}

fun Calendar.isYesterday(): Boolean {
    val calendar = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -1)
    }
    return isCurrentYear() && get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)
}