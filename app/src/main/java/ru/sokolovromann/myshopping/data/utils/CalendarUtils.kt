package ru.sokolovromann.myshopping.data.utils

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