package ru.sokolovromann.myshopping.core.domain.model

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@JvmInline
value class TimeInMillis(val value: Long) {

    companion object {
        fun getCurrent(): TimeInMillis = TimeInMillis(System.currentTimeMillis())
    }

    fun plus(millis: Long): TimeInMillis? = try {
        TimeInMillis(value.plus(millis))
    } catch (_: Exception) { null }

    fun minus(millis: Long): TimeInMillis? = try {
        TimeInMillis(value.minus(millis))
    } catch (_: Exception) { null }

    fun getDisplay(): String {
        val zoneId = ZoneId.systemDefault()
        val now = Instant.now().atZone(zoneId).toLocalDate()
        val target = Instant.ofEpochMilli(value).atZone(zoneId)
        val targetDate = target.toLocalDate()
        val locale = Locale.getDefault()
        val timePatter = when (locale) {
            Locale.US -> "hh:mm a"
            else -> "HH:mm"
        }
        return when {
            targetDate.isEqual(now) -> {
                target.format(DateTimeFormatter.ofPattern(timePatter, locale))
            }
            targetDate.year == now.year -> {
                val datePattern = when (locale) {
                    Locale.US -> "MMM dd, "
                    Locale.CANADA -> "MMM dd "
                    else -> "dd MMM "
                }
                target.format(DateTimeFormatter.ofPattern("$datePattern$timePatter", locale))
            }
            else -> {
                val datePattern = when (locale) {
                    Locale.US -> "MMM dd yy, "
                    Locale.CANADA -> "yy MMM dd "
                    else -> "dd MMM yy"
                }
                target.format(DateTimeFormatter.ofPattern("$datePattern$timePatter", locale))
            }
        }
    }

    fun getDisplay(pattern: String): String = Instant
        .ofEpochMilli(value)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern(pattern, Locale.getDefault()))
}