package ru.sokolovromann.myshopping.core.domain.model

@JvmInline
value class TimeInMillis(val value: Long) {

    init {
        require(value >= MIN.value && value <= MAX.value) {
            "The value must be greater than or equal ${MIN.value} and less than ${MAX.value}."
        }
    }

    companion object {
        val MIN: TimeInMillis = TimeInMillis(1L)
        val MAX: TimeInMillis = TimeInMillis(Long.MAX_VALUE)
        fun getCurrent(): TimeInMillis = TimeInMillis(System.currentTimeMillis())
    }

    fun plus(millis: Long): TimeInMillis? = try {
        TimeInMillis(value.plus(millis))
    } catch (_: Exception) { null }

    fun minus(millis: Long): TimeInMillis? = try {
        TimeInMillis(value.minus(millis))
    } catch (_: Exception) { null }
}