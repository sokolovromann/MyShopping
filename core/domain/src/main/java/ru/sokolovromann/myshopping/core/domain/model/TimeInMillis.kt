package ru.sokolovromann.myshopping.core.domain.model

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
}